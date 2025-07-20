package rinha.db;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class PaymentRepository implements PanacheRepositoryBase<PaymentEntity, UUID> {

    public void updateToProcessed(UUID id, ProcessorType processor) {
        update(
                """
                        processor = :processor,
                        status = :status
                        where correlationId = :id
                      """,
                Parameters
                        .with("processor", processor)
                        .and("status", PaymentStatus.PROCESSED)
                        .and("id", id)
        );
    }

}
