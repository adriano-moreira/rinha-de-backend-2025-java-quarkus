package rinha.features.payment;

import jakarta.enterprise.context.ApplicationScoped;
import rinha.bridge.processor.PaymentProcessorPayload;
import rinha.features.processor.ProcessorService;

import java.time.LocalDateTime;

@ApplicationScoped
public class ReceiveUserCase {

    private final ProcessorService processorService;

    public ReceiveUserCase(ProcessorService processorService) {
        this.processorService = processorService;
    }

    public void receive(PaymentDTO payment) {
        var dto = new PaymentProcessorPayload();
        dto.setCorrelationId(payment.getCorrelationId());
        dto.setAmount(payment.getAmount());
        dto.setRequestedAt(LocalDateTime.now());
        processorService.sendToQueue(dto);
    }

}
