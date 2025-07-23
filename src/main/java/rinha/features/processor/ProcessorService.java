package rinha.features.processor;

import io.quarkus.logging.Log;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import rinha.bridge.processor.PaymentProcessorPayload;
import rinha.bridge.processor.ProcessorDefault;
import rinha.bridge.processor.ProcessorFallback;
import rinha.entities.PaymentEntity;
import rinha.entities.PaymentStatus;
import rinha.entities.ProcessorType;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class ProcessorService {

    private final ConcurrentLinkedQueue<PaymentProcessorPayload> queue = new ConcurrentLinkedQueue<>();

    private final ProcessorDefault defaultClient;
    private final ProcessorFallback fallbackClient;
    private final ProcessorRepository processorRepository;

    private final AtomicInteger counter = new AtomicInteger();
    private final AtomicBoolean defaultOK = new AtomicBoolean(true);
    private final AtomicBoolean fallbackOk = new AtomicBoolean(true);
    private ScheduledExecutorService scheduler;
    private ExecutorService executorVT;
    private boolean shutdown = false;

    public ProcessorService(
            @RestClient
            ProcessorDefault defaultClient,
            @RestClient
            ProcessorFallback fallbackClient,
            ProcessorRepository processorRepository
    ) {
        this.defaultClient = defaultClient;
        this.fallbackClient = fallbackClient;
        this.processorRepository = processorRepository;
    }

    public void onStart(@Observes StartupEvent event) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(this::tick, 50, TimeUnit.MILLISECONDS);

        executorVT = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void onStop(@Observes ShutdownEvent event) {
        Log.info("Shutting down, queue size: " + queue.size());
        shutdown = true;
        scheduler.shutdown();
        executorVT.shutdown();
    }

    private boolean queueNotEmpty() {
        return !queue.isEmpty();
    }

    private boolean notSaturated() {
        return counter.get() < 24;
    }

    private boolean processorsOk() {
        return defaultOK.get() && fallbackOk.get();
    }

    private void tick() {
        executorVT.submit(() -> {
            while (queueNotEmpty() && notSaturated() && processorsOk()) {
                executorVT.submit(() -> {
                    PaymentProcessorPayload headElement = queue.poll();
                    if (headElement == null) {
                        return;
                    }
                    counter.incrementAndGet();
                    sendToProcessor(headElement);
                    counter.decrementAndGet();
                });
            }
            if (shutdown) {
                return;
            }
            scheduler.schedule(this::tick, 50, TimeUnit.MILLISECONDS);
        });
    }

    public void sendToQueue(PaymentProcessorPayload payload) {
        queue.add(payload);
    }

    public void sendToProcessor(PaymentProcessorPayload payload) {
        var processor = sendToProcessorImpl(payload);
        if (processor != null) {
            update(payload, processor);
        } else {
            //resend to queue
            sendToQueue(payload);
        }
    }

    private ProcessorType sendToProcessorImpl(PaymentProcessorPayload payload) {
        if (defaultOK.get()) {
            try {
                defaultClient.process(payload);
                return ProcessorType.DEFAULT;
            } catch (Exception ex) {
                defaultOK.set(false);
                scheduler.schedule(() -> defaultOK.set(true), 400, TimeUnit.MILLISECONDS);
            }
        }

        if (fallbackOk.get()) {
            try {
                fallbackClient.process(payload);
                return ProcessorType.FALLBACK;
            } catch (Exception ex) {
                fallbackOk.set(false);
                scheduler.schedule(() -> fallbackOk.set(true), 400, TimeUnit.MILLISECONDS);
            }
        }

        return null;
    }


    @Transactional
    public void update(PaymentProcessorPayload payload, ProcessorType type) {
        PaymentEntity entity = new PaymentEntity();
        entity.setCorrelationId(payload.getCorrelationId());
        entity.setAmount(payload.getAmount());
        entity.setRequestedAt(payload.getRequestedAt());
        entity.setProcessor(type);
        entity.setStatus(PaymentStatus.PROCESSED);
        processorRepository.persist(entity);
    }

}
