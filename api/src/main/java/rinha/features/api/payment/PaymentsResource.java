package rinha.features.api.payment;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("payments")
public class PaymentsResource {

    private final Pool pool;

    public PaymentsResource(Pool pool) {
        this.pool = pool;
    }

    @POST
    public Uni<Response> receive(PaymentDTO payment) {
        return Uni
                .createFrom().item(Response.ok().build())
                .onTermination()
                .invoke(() -> persist(payment));
    }

    private void persist(PaymentDTO payment) {
        var insert = "insert into payments (amount,processor,requested_at,status,correlation_id) values ($1,null,now(),0,$2)";
        var values = Tuple.of(payment.getAmount(), payment.getCorrelationId());
        pool.preparedQuery(insert).execute(values)
                .emitOn(Infrastructure.getDefaultExecutor())
                .subscribe().with(
                        this::doNothing,
                        this::logError
                );
    }

    private void doNothing(Object object) {
    }

    private void logError(Throwable throwable) {
        Log.error("persist fail", throwable);
    }

}
