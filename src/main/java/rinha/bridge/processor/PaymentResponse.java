package rinha.bridge.processor;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class PaymentResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
