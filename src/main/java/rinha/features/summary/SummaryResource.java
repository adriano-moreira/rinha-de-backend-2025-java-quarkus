package rinha.features.summary;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.util.Date;

@Path("/payments-summary")
public class SummaryResource {

    private final SummaryUserCase summaryUserCase;

    public SummaryResource(SummaryUserCase summaryUserCase) {
        this.summaryUserCase = summaryUserCase;
    }

    @GET()
    @RunOnVirtualThread
    public SummaryDTO getPaymentsSummary(
            @QueryParam("from") Date from,
            @QueryParam("to") Date to
    ) {
        return summaryUserCase.summary(from, to);
    }
}
