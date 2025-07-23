package rinha.features.payment;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("payments")
public class PaymentsResource {

    private final ReceiveUserCase receiveUserCase;

    public PaymentsResource(ReceiveUserCase receiveUserCase) {
        this.receiveUserCase = receiveUserCase;
    }

    @POST
    @RunOnVirtualThread
    public Response receive(PaymentDTO payload) {
        receiveUserCase.receive(payload);
        return Response.ok().build();
    }

}
