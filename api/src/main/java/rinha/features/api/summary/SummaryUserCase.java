package rinha.features.api.summary;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;

@ApplicationScoped
public class SummaryUserCase {

    private final SummaryRepository summaryRepository;

    public SummaryUserCase(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    public Uni<SummaryDTO> summary(String from, String to) {
        return Uni.createFrom()
                .item(() -> {
                    var dtFrom = parseDate(from);
                    var dtTo = parseDate(to);
                    return Tuple2.of(dtFrom, dtTo);
                })
                .flatMap(t -> summary(t.getItem1(), t.getItem2()));
    }

    public Uni<SummaryDTO> summary(LocalDateTime from, LocalDateTime to) {
        if (from == null) {
            from = LocalDateTime.MIN;
        }
        if (to == null) {
            to = LocalDateTime.MAX;
        }
        return summaryRepository.summary(from, to);
    }

    public LocalDateTime parseDate(String date) {
        if (date == null) return null;
        return LocalDateTime.parse(date.replace("Z", ""));
    }
}
