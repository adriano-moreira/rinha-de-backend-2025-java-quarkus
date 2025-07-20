package rinha.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment")
public class PaymentEntity {

    @Id
    private UUID correlationId;

    private BigDecimal amount;

    private LocalDateTime requestedAt;

    private PaymentStatus status;

    private ProcessorType processor;

    public PaymentEntity() {
    }

    public PaymentEntity(UUID correlationId, BigDecimal amount) {
        this.correlationId = correlationId;
        this.amount = amount;
        this.requestedAt = LocalDateTime.now();
        this.status = PaymentStatus.CREATED;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public ProcessorType getProcessor() {
        return processor;
    }

    public void setProcessor(ProcessorType processorType) {
        this.processor = processorType;
    }
}
