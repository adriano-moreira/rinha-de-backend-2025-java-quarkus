package rinha.features.summary;

import jakarta.enterprise.context.ApplicationScoped;
import rinha.dto.PaymentsSummary;

import java.util.Date;

@ApplicationScoped
public class SummaryUserCase {

    private final SummaryRepository summaryRepository;

    public SummaryUserCase(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    public PaymentsSummary summary(Date from, Date to) {
        if (from == null) {
            from = new Date(0);
        }
        if (to == null) {
            to = new Date();
        }
        return summaryRepository.summary(from, to);
    }
}
