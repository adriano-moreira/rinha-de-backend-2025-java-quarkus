package rinha.features.payment;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import rinha.bridge.processor.PaymentProcessorPayload;
import rinha.features.processor.ProcessorService;

import java.time.LocalDateTime;

@Path("payments")
public class PaymentsResource {

    private final ProcessorService processorService;

    public PaymentsResource(ProcessorService processorService) {
        this.processorService = processorService;
    }

    @POST
    @RunOnVirtualThread
    public Response receive(PaymentDTO payment) {
        var dto = new PaymentProcessorPayload();
        dto.setCorrelationId(payment.getCorrelationId());
        dto.setAmount(payment.getAmount());
        dto.setRequestedAt(LocalDateTime.now());
        processorService.sendToQueue(dto);
        return Response.ok().build();
    }

}
