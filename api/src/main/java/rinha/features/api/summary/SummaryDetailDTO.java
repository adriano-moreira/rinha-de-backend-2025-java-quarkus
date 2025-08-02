package rinha.features.api.summary;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.math.BigDecimal;

@RegisterForReflection
public class SummaryDetailDTO {
    private Long totalRequests = 0L;
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public void increase(BigDecimal amount) {
        this.totalAmount = totalAmount.add(amount);
        this.totalRequests++;
    }

    public Long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "{" +
                "totalRequests=" + totalRequests +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
