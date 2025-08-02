package rinha.features.worker;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import rinha.bridge.processor.PaymentProcessorPayload;
import rinha.bridge.processor.ProcessorDefault;
import rinha.bridge.processor.ProcessorFallback;

import java.time.Duration;
import java.util.List;

import static rinha.features.worker.WorkerRepository.PROCESSOR_DEFAULT;
import static rinha.features.worker.WorkerRepository.PROCESSOR_FALLBACK;

@ApplicationScoped
public class WorkerService {

    private final ProcessorDefault defaultProcessor;
    private final ProcessorFallback fallbackProcessor;
    private final WorkerRepository repository;

    boolean useDefaultProcessor = true;
    boolean useFallbackProcessor = true;

    @Inject
    public WorkerService(
            @RestClient
            ProcessorDefault defaultProcessor,
            @RestClient
            ProcessorFallback fallbackProcessor,
            WorkerRepository repository
    ) {
        this.defaultProcessor = defaultProcessor;
        this.fallbackProcessor = fallbackProcessor;
        this.repository = repository;
    }

    ///  return true when 2 processors are disabled
    private boolean withoutProcessors() {
        return !useDefaultProcessor && !useFallbackProcessor;
    }

    /// listener fired on startup
    public void startWorker(@Observes StartupEvent event) {
        new Thread(this::worker).start();
    }

    public void worker() {
        Multi.createBy()
                .repeating()
                .uni(this::mainLoop)
                .indefinitely()
                .subscribe().with(
                        v -> {
                        },
                        t -> Log.error("fail on loop", t)
                );
    }

    /// the main loop of the worker 😁, run one per time, 0 concurrency
    private Uni<?> mainLoop() {
        return repository
                /// return Uni<List<PaymentProcessoPayload>>
                .getOnePageOfUnprocessed(100L)
                .call(this::delayIfListEmpty)
                /// convert to Multi<PaymentProcessoPayload>
                .onItem().transformToMulti(Multi.createFrom()::iterable)
                /// transform to uni each item 8 per time
                .onItem().transformToUni(this::processPayment).merge(12)
                ///  await finish Multi<>, and return last as a Uni<>
                .collect().asList();
    }

    private Uni<?> delayIfListEmpty(List<PaymentProcessorPayload> items) {
        if (items.isEmpty()) {
            return Uni.createFrom().nullItem()
                    .onItem().delayIt().by(Duration.ofMillis(20))
                    ;
        }
        return Uni.createFrom().voidItem();
    }

    private Uni<Integer> processPayment(PaymentProcessorPayload payment) {
        /// send to processors api
        return sendToProcessor(payment)
                .call(processor -> repository.update(payment.getCorrelationId(), processor))
                /// case fail retry until succeed 🔥🔥🔥
                .onFailure().retry().indefinitely();
    }

    /// call default/fallback and persiste when success
    private Uni<Integer> sendToProcessor(PaymentProcessorPayload payment) {
//
//        /// if without processors api enabled wait to not saturate CPU,
//        /// awaiting at least one processor enabled
//        if (withoutProcessors()) {
//            return Uni.createFrom().item(0)
//                    .onItem().delayIt().by(Duration.ofMillis(50))
//                    .onItem().failWith(() -> new RuntimeException("no processor active"));
//        }
//
//        /// send to default, if failed send to fallback
//        if (useDefaultProcessor && useFallbackProcessor) {
            return sendToDefaultProcessor(payment)
                    .map(r -> PROCESSOR_DEFAULT)
                    .onFailure().recoverWithUni(
                            () -> sendToFallbackProcessor(payment)
                                    .map(response -> PROCESSOR_FALLBACK)
                    );
//        }
//
//        /// send to fallback only
//        if (useFallbackProcessor) {
//            return sendToFallbackProcessor(payment)
//                    .map(v -> PROCESSOR_FALLBACK);
//        }
//
//        /// send to default only
//        return sendToDefaultProcessor(payment).map(v -> PROCESSOR_DEFAULT);

    }

    private Uni<Response> sendToDefaultProcessor(PaymentProcessorPayload payment) {
        return defaultProcessor.send(payment)
//                .onFailure().invoke(this::disableDefaultTemporarily)
                ;
    }


    private Uni<Response> sendToFallbackProcessor(PaymentProcessorPayload payment) {
        return fallbackProcessor.send(payment)
//                .onFailure().invoke(this::disableFallbackTemporarily)
                ;
    }

    private void disableDefaultTemporarily(Throwable throwable) {
        Log.info("default temporarily disabled");
        useDefaultProcessor = false;
        Uni.createFrom().voidItem()
                .emitOn(Infrastructure.getDefaultExecutor())
                .onItem().delayIt().by(Duration.ofMillis(200))
                .invoke(() -> {
                    Log.info("default enabled");
                    useDefaultProcessor = true;
                })
                .subscribe().with(unused -> {
                });
    }

    private void disableFallbackTemporarily(Throwable throwable) {
        Log.info("fallback temporarily disabled");
        useFallbackProcessor = false;
        Uni.createFrom().voidItem()
                .emitOn(Infrastructure.getDefaultExecutor())
                .onItem().delayIt().by(Duration.ofMillis(200))
                .invoke(() -> {
                    Log.info("fallback enabled");
                    useFallbackProcessor = true;
                })
                .subscribe().with(unused -> {
                });
    }
}
