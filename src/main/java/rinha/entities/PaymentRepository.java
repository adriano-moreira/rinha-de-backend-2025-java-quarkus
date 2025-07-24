package rinha.entities;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class PaymentRepository implements PanacheRepositoryBase<PaymentEntity, UUID> {
}
