package rinha.bridge.processor;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ProcessorServiceHealth {
    private boolean failing;
    private Long minResponseTime;

    public boolean isFailing() {
        return failing;
    }

    public void setFailing(boolean failing) {
        this.failing = failing;
    }

    public Long getMinResponseTime() {
        return minResponseTime;
    }

    public void setMinResponseTime(Long minResponseTime) {
        this.minResponseTime = minResponseTime;
    }
}
