package rinha.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @Column(name = "correlation_id")
    private UUID correlationId;

    private BigDecimal amount;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    private PaymentStatus status;

    private ProcessorType processor;

    public PaymentEntity() {
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

    @Override
    public String toString() {
        return "PaymentEntity{" +
                "correlationId=" + correlationId +
                ", amount=" + amount +
                ", requestedAt=" + requestedAt +
                ", status=" + status +
                ", processor=" + processor +
                '}';
    }
}
