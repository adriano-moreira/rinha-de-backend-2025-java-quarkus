package rinha.features.worker;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlResult;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import rinha.bridge.processor.PaymentProcessorPayload;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class WorkerRepository {

    private final Pool pool;
    private final static Integer STATUS_CREATED = 0;
    private final static Integer STATUS_PROCESSED = 1;
    public final static Integer PROCESSOR_DEFAULT = 0;
    public static Integer PROCESSOR_FALLBACK = 1;

    private final static String select = """
            SELECT correlation_id, amount, requested_at
            FROM payments
            WHERE status = $1
            LIMIT $2
            FOR UPDATE SKIP LOCKED
            """;

    private final static String update = "update payments set processor = $2, status = $3 where correlation_id = $1";

    public WorkerRepository(Pool pool) {
        this.pool = pool;
    }

    public Uni<List<PaymentProcessorPayload>> getOnePageOfUnprocessed(Long limit) {
        return pool.preparedQuery(select)
                .mapping(this::rowToPayload)
                .execute(Tuple.of(STATUS_CREATED, limit))
                .map(rs -> rs.stream().toList());
    }

    private PaymentProcessorPayload rowToPayload(Row row) {
        PaymentProcessorPayload payload = new PaymentProcessorPayload();
        payload.setCorrelationId(row.getUUID("correlation_id"));
        payload.setAmount(row.getBigDecimal("amount"));
        payload.setRequestedAt(row.getLocalDateTime("requested_at"));
        return payload;
    }

    public Uni<Integer> update(UUID id, Integer processor) {
        var values = Tuple.of(id, processor, STATUS_PROCESSED);
        return pool.withTransaction(
                        conn ->
                                conn.preparedQuery(update)
                                        .execute(values)
                                        .map(SqlResult::rowCount)
                )
                .onFailure().invoke(t -> Log.errorv("fail update error {0}:", id, t));

    }

}
