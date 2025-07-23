package rinha.features.summary;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@ApplicationScoped
public class SummaryUserCase {

    private final SummaryRepository summaryRepository;
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    public SummaryUserCase(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    public SummaryDTO summary(String from, String to) {
        try {
            var dtFrom = from == null ? null : sdf.parse(from);
            var dtTo = to == null ? null : sdf.parse(to);
            return summary(dtFrom, dtTo);
        } catch (ParseException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public SummaryDTO summary(Date from, Date to) {
        if (from == null) {
            from = new Date(0);
        }
        if (to == null) {
            to = new Date();
        }
        return summaryRepository.summary(from, to);
    }
}
