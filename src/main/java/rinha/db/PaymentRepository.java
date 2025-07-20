package rinha.db;

import io.agroal.api.AgroalDataSource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import rinha.dto.PaymentsSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;

@ApplicationScoped
public class PaymentRepository implements PanacheRepositoryBase<PaymentEntity, UUID> {

    public void updateProcessor(UUID id, ProcessorType type) {
        update(
                "processor = :processor where correlationId = :id",
                Parameters.with("id", id)
                        .and("processor", type.ordinal())
        );
    }
}
