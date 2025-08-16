package rinha.features.api.summary;

import io.quarkus.vertx.web.Route;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApplicationScoped
public class SummaryResource {

    private final Pool pool;
    private static final Integer DEFAULT = 0;
    private static final Integer FALLBACK = 1;

    public SummaryResource(Pool pool) {
        this.pool = pool;
    }

    @Route(path = "/payments-summary", methods = Route.HttpMethod.GET)
    public void getPaymentsSummary(RoutingContext rc) {
            var request = rc.request();
            String from = request.getParam("from");
            String param = request.getParam("to");

            getSummary(pool, from, param)
                    .map(rc::json);
    }

    private final String selectSummary = """
            select processor, sum(amount) total_amount, count(*) total_requests
            from payments
            where processor is not null
            and requested_at between $1 and $2
            group by processor
            """;

    public LocalDateTime parseDate(String date) {
        if (date == null) return null;
        return LocalDateTime.parse(date.replace("Z", ""));
    }

    public Future<JsonObject> getSummary(Pool pool, String from, String to) {
        return getSummary(pool, parseDate(from), parseDate(to));
    }

    public Future<JsonObject> getSummary(Pool pool, LocalDateTime from, LocalDateTime to) {
        if (from == null) {
            from = LocalDateTime.MIN;
        }
        if (to == null) {
            to = LocalDateTime.MAX;
        }
        return pool.preparedQuery(selectSummary)
                .execute(Tuple.of(from, to))
                .map(this::toJsonSummary)
                .onFailure(Throwable::printStackTrace);

    }

    private JsonObject toJsonSummary(RowSet<Row> rowSet) {
        var defaultTotalAmount = BigDecimal.ZERO;
        var ddefaultTotalRequests = 0L;
        var fallbackTotalAmount = BigDecimal.ZERO;
        var fallbackTotalRequests = 0L;

        for (Row row : rowSet) {
            if (DEFAULT.equals(row.getInteger("processor"))) {
                defaultTotalAmount = row.getBigDecimal("total_amount");
                ddefaultTotalRequests = row.getLong("total_requests");
            }
            if (FALLBACK.equals(row.getInteger("processor"))) {
                fallbackTotalAmount = row.getBigDecimal("total_amount");
                fallbackTotalRequests = row.getLong("total_requests");
            }
        }

        JsonObject summary = new JsonObject();
        JsonObject defaultObject = new JsonObject();
        defaultObject.put("totalAmount", defaultTotalAmount);
        defaultObject.put("totalRequests", ddefaultTotalRequests);
        JsonObject fallbackObject = new JsonObject();
        fallbackObject.put("totalAmount", fallbackTotalAmount);
        fallbackObject.put("totalRequests", fallbackTotalRequests);
        summary.put("default", defaultObject);
        summary.put("fallback", fallbackObject);
        return summary;
    }

}
