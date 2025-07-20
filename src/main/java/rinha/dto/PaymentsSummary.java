package rinha.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class PaymentsSummary {

    @JsonProperty("default")
    private Summary defaultSummary = new Summary();

    @JsonProperty("fallback")
    private Summary fallbackSummary = new Summary();

    public Summary getDefaultSummary() {
        return defaultSummary;
    }

    public void setDefaultSummary(Summary defaultSummary) {
        this.defaultSummary = defaultSummary;
    }

    public Summary getFallbackSummary() {
        return fallbackSummary;
    }

    public void setFallbackSummary(Summary fallbackSummary) {
        this.fallbackSummary = fallbackSummary;
    }
}
