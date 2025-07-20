package rinha.features.summary;

import io.agroal.api.AgroalDataSource;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import rinha.db.ProcessorType;

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
            from payments
            where processor is not null
            and requested_at between ? and ?
            group by processor
            """;


    public SummaryDTO summary(Date from, Date to) {
        try (
                Connection connection = dataSource.getConnection()
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(summaryQuery);
            preparedStatement.setDate(1, new java.sql.Date(from.getTime()));
            preparedStatement.setDate(2, new java.sql.Date(to.getTime()));
            ResultSet resultSet = preparedStatement.executeQuery();

            SummaryDTO summary = new SummaryDTO();
            while (resultSet.next()) {
                if (resultSet.getInt("processor") == ProcessorType.DEFAULT.ordinal()) {
                    summary.getDefaultSummary().setTotalAmount(resultSet.getBigDecimal("totalAmount"));
                    summary.getDefaultSummary().setTotalRequests(resultSet.getLong("totalRequests"));
                }
                if (resultSet.getInt("processor") == ProcessorType.FALLBACK.ordinal()) {
                    summary.getFallbackSummary().setTotalAmount(resultSet.getBigDecimal("totalAmount"));
                    summary.getFallbackSummary().setTotalRequests(resultSet.getLong("totalRequests"));
                }
            }
            resultSet.close();
            preparedStatement.close();
            return summary;
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }


}
