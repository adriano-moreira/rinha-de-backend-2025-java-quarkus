package rinha.bridge.processor;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("")
@RegisterRestClient(configKey = "processor-fallback")
public interface ProcessorFallback {

    @POST
    @Path("/payments")
    PaymentResponse process(PaymentProcessorPayload paymentPayload);

    @GET
    @Path("/payments/service-health")
    Response healthCheck();

}
