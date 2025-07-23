package rinha.features.summary;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("/payments-summary")
public class SummaryResource {

    private final SummaryUserCase summaryUserCase;

    public SummaryResource(SummaryUserCase summaryUserCase) {
        this.summaryUserCase = summaryUserCase;
    }

    @GET()
    @RunOnVirtualThread
    public SummaryDTO getPaymentsSummary(
            @QueryParam("from") String from,
            @QueryParam("to") String to
    ) {
        return summaryUserCase.summary(from, to);
    }
}
