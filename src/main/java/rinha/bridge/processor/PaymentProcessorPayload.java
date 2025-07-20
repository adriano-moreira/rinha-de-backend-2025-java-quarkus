package rinha.bridge.processor;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RegisterForReflection
public class PaymentProcessorPayload {

    private UUID correlationId;
    private BigDecimal amount;
    private LocalDateTime requestedAt;

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


    @Override
    public String toString() {
        return "PaymentProcessorPayload{" +
                "correlationId=" + correlationId +
                ", amount=" + amount +
                ", requestedAt=" + requestedAt +
                '}';
    }
}
