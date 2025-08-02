package rinha.features.api.summary;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;

@ApplicationScoped
public class SummaryRepository {

    private final Pool pool;
    private static final Integer DEFAULT = 0;
    private static final Integer FALLBACK = 1;

    public SummaryRepository(Pool dataSource) {
        this.pool = dataSource;
    }

    final String summaryQuery = """
            select processor, sum(amount) total_amount, count(*) total_requests
            from payments
            where processor is not null
            and requested_at between $1 and $2
            group by processor
            """;

    public Uni<SummaryDTO> summary(LocalDateTime from, LocalDateTime to) {
        var params = Tuple.of(from, to);
        return pool.preparedQuery(summaryQuery).execute(params)
                .map(rowSet -> {
                    SummaryDTO summary = new SummaryDTO();
                    rowSet.forEach(row -> {
                        if (DEFAULT.equals(row.getInteger("processor"))) {
                            summary.getDefaultSummary().setTotalAmount(row.getBigDecimal("total_amount"));
                            summary.getDefaultSummary().setTotalRequests(row.getLong("total_requests"));
                        }
                        if (FALLBACK.equals(row.getInteger("processor"))) {
                            summary.getFallbackSummary().setTotalAmount(row.getBigDecimal("total_amount"));
                            summary.getFallbackSummary().setTotalRequests(row.getLong("total_requests"));
                        }
                    });
                    return summary;
                });
    }


}
