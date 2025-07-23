package rinha.features.payment;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import rinha.entities.PaymentEntity;
import rinha.entities.PaymentStatus;
import rinha.entities.ProcessorType;

import java.util.UUID;

@ApplicationScoped
public class PaymentRepository implements PanacheRepositoryBase<PaymentEntity, UUID> {


    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Override
    public void persist(PaymentEntity entity) {
        PanacheRepositoryBase.super.persist(entity);
    }

}
