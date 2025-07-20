package rinha.bridge.processor;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class PaymentSummary {

    private Long totalRequests;
    private Double totalAmount;
    private Double totalFee;
    private Double feePerTransaction;


    public Long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Double totalFee) {
        this.totalFee = totalFee;
    }

    public Double getFeePerTransaction() {
        return feePerTransaction;
    }

    public void setFeePerTransaction(Double feePerTransaction) {
        this.feePerTransaction = feePerTransaction;
    }
}
