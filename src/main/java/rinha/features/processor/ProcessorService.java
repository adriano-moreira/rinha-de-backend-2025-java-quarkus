package rinha.features.processor;

import io.quarkus.logging.Log;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import rinha.bridge.processor.ProcessorDefault;
import rinha.bridge.processor.PaymentProcessorPayload;
import rinha.db.PaymentRepository;
import rinha.db.ProcessorType;

import java.util.UUID;

@ApplicationScoped
public class ProcessorService {

    @RestClient
    ProcessorDefault defaultClient;

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    EventBus eventBus;

    public void send(PaymentProcessorPayload payload) {
        this.eventBus.request("send-to-processor", payload);
    }

    @ConsumeEvent("send-to-processor")
    @Blocking
    public void sendToProcessor(PaymentProcessorPayload payload) {
        Log.infov("processing {0}",payload.toString());
        try {
            defaultClient.process(payload);
            update(payload.getCorrelationId(), ProcessorType.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
            //send(payload); //add to queue again
        }
    }

    @Transactional
    public void update(UUID uuid, ProcessorType type) {
        paymentRepository.updateToProcessed(uuid, type);
    }

}
