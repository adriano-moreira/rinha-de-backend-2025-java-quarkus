package rinha.features.summary;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import rinha.dto.PaymentsSummary;

import java.util.Date;

@Path("/payments-summary")
public class PaymentSummaryResource {

    private final SummaryUserCase summaryUserCase;

    public PaymentSummaryResource(SummaryUserCase summaryUserCase) {
        this.summaryUserCase = summaryUserCase;
    }

    @GET()
    @RunOnVirtualThread
    public PaymentsSummary getPaymentsSummary(
            @QueryParam("from") Date from,
            @QueryParam("to") Date to
    ) {
        return summaryUserCase.summary(from, to);
    }
}
