package rinha.features.api.payment;

import io.quarkus.vertx.web.Route;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaymentsResource {

    private final Pool pool;

    public PaymentsResource(Pool pool) {
        this.pool = pool;
    }

    @Route(path = "/payments")
    public void receive(RoutingContext rc) {
        rc.response().end();
                persist(rc.body().asJsonObject());
    }

    private void persist(JsonObject json) {
        var insert = "insert into payments (amount,processor,requested_at,status,correlation_id) values ($1,null,now(),0,$2)";
        var values = Tuple.of(
                json.getDouble("amount"),
                json.getString("correlationId")
        );
        pool.preparedQuery(insert).execute(values);
    }

}
