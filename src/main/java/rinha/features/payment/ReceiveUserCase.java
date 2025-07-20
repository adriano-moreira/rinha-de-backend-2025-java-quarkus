package rinha.features.payment;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import rinha.bridge.processor.PaymentProcessorPayload;
import rinha.db.PaymentEntity;
import rinha.db.PaymentRepository;
import rinha.features.processor.ProcessorService;

@ApplicationScoped
public class ReceiveUserCase {

    private final PaymentRepository paymentRepository;
    private final ProcessorService processorService;

    public ReceiveUserCase(PaymentRepository paymentRepository, ProcessorService processorService) {
        this.paymentRepository = paymentRepository;
        this.processorService = processorService;
    }

    public void receive(PaymentDTO payment) {
        if (payment == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (payment.getCorrelationId() == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (payment.getAmount() == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        //TODO: if = 0
        //TODO: if value negative

        var entity = new PaymentEntity(payment.getCorrelationId(), payment.getAmount());
        persist(entity);

        var dto = new PaymentProcessorPayload();
        dto.setCorrelationId(payment.getCorrelationId());
        dto.setAmount(payment.getAmount());
        dto.setRequestedAt(entity.getRequestedAt());
        processorService.send(dto);
    }

    @Transactional
    public void persist(PaymentEntity entity) {
        paymentRepository.persist(entity);
    }

}
