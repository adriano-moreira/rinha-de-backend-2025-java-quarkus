package rinha.features.payment;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import rinha.dto.PaymentDTO;

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
