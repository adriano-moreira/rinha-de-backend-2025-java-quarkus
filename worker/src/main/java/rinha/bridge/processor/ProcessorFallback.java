package rinha.bridge.processor;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("")
@RegisterRestClient(configKey = "processor-fallback")
public interface ProcessorFallback {

    @POST
    @Path("/payments")
    Uni<Response> send(PaymentProcessorPayload paymentPayload);

}
