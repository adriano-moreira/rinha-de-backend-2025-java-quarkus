package rinha.features.api.summary;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SummaryDTO {

    @JsonProperty("default")
    private SummaryDetailDTO defaultSummary = new SummaryDetailDTO();

    @JsonProperty("fallback")
    private SummaryDetailDTO fallbackSummary = new SummaryDetailDTO();

    public SummaryDetailDTO getDefaultSummary() {
        return defaultSummary;
    }

    public void setDefaultSummary(SummaryDetailDTO defaultSummary) {
        this.defaultSummary = defaultSummary;
    }

    public SummaryDetailDTO getFallbackSummary() {
        return fallbackSummary;
    }

    public void setFallbackSummary(SummaryDetailDTO fallbackSummary) {
        this.fallbackSummary = fallbackSummary;
    }

    @Override
    public String toString() {
        return "SummaryDTO{" +
                "\ndefault=" + defaultSummary +
                "\nfallback=" + fallbackSummary +
                '}';
    }
}
