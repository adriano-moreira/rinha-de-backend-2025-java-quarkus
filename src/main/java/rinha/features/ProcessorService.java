package rinha.features;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import rinha.bridge.processor.PaymentProcessorClient;
import rinha.bridge.processor.PaymentProcessorPayload;
import rinha.db.PaymentRepository;
import rinha.db.ProcessorType;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@ApplicationScoped
public class ProcessorService {

    private final ConcurrentLinkedQueue<PaymentProcessorPayload> queue = new ConcurrentLinkedQueue<>();

    @RestClient
    PaymentProcessorClient defaultClient;

    @Inject
    PaymentRepository paymentRepository;

    public void send(PaymentProcessorPayload payload) {
        queue.add(payload);
    }

    @Scheduled(every = "1s")
    public void run() {
        if (queue.isEmpty()) {
            return;
        }
        queue.forEach(this::process);
    }

    private void process(PaymentProcessorPayload payload) {
        //Log.infov("Processing {0}", payload);

        try {
            defaultClient.process(payload);
            update(payload.getCorrelationId(), ProcessorType.DEFAULT);
        } catch (Exception ex) {
            send(payload); //add to queue again
        }
        defaultClient.process(payload);
    }

    @Transactional
    public void update(UUID uuid, ProcessorType type) {
        paymentRepository.updateProcessor(uuid, type);
    }

}
