package rinha.features.summary;

import io.agroal.api.AgroalDataSource;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import rinha.db.ProcessorType;
import rinha.dto.PaymentsSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

@ApplicationScoped
public class SummaryRepository {

    private final AgroalDataSource dataSource;

    public SummaryRepository(AgroalDataSource dataSource) {
        this.dataSource = dataSource;
    }

    final String summaryQuery = """
            select processor, sum(amount) totalAmount, count(*) totalRequests
            from payment
            where processor in (0, 1)
            and requestedAt between ? and ?
            group by processor
            """;


    public PaymentsSummary summary(Date from, Date to) {
        try (
                Connection connection = dataSource.getConnection()
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(summaryQuery);
            preparedStatement.setDate(1, new java.sql.Date(from.getTime()));
            preparedStatement.setDate(2, new java.sql.Date(to.getTime()));
            ResultSet resultSet = preparedStatement.executeQuery();

            PaymentsSummary paymentsSummary = new PaymentsSummary();
            while (resultSet.next()) {
                if (resultSet.getInt("processor") == ProcessorType.DEFAULT.ordinal()) {
                    paymentsSummary.getDefaultSummary().setTotalAmount(resultSet.getBigDecimal("totalAmount"));
                    paymentsSummary.getDefaultSummary().setTotalRequests(resultSet.getLong("totalRequests"));
                }
                if (resultSet.getInt("processor") == ProcessorType.FALLBACK.ordinal()) {
                    paymentsSummary.getFallbackSummary().setTotalAmount(resultSet.getBigDecimal("totalAmount"));
                    paymentsSummary.getFallbackSummary().setTotalRequests(resultSet.getLong("totalRequests"));
                }
            }
            resultSet.close();
            preparedStatement.close();
            return paymentsSummary;
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }


}
