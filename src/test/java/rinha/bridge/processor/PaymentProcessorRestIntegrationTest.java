package rinha.bridge.processor;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import rinha.db.PaymentEntity;
import rinha.db.PaymentRepository;
import rinha.db.PaymentStatus;
import rinha.db.ProcessorType;
import rinha.features.summary.SummaryDTO;
import rinha.features.summary.SummaryUserCase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@QuarkusTest
public class PaymentProcessorRestIntegrationTest {

    @RestClient
    ProcessorDefault client;

    @Test
    @Disabled
    void payTest() {

            var payment = new PaymentProcessorPayload();
            payment.setAmount(BigDecimal.valueOf(20L));
            payment.setCorrelationId(UUID.randomUUID());
            payment.setRequestedAt(LocalDateTime.now());
            var resp = client.process(payment);
//            Log.info(resp);
//            Log.info(resp.getMessage());
            Assertions.assertEquals("payment processed successfully", resp.getMessage());
    }

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    SummaryUserCase summaryRepository;

    @Test
    @Transactional
    @Disabled
    void persistTest() {


        createData();
        SummaryDTO summary = summaryRepository.summary(null, null);
        Log.infov("found summary {0}", summary);

    }

    public void createData() {

        {
            UUID paymentId = UUID.randomUUID();

            PaymentEntity entity = new PaymentEntity();
            entity.setStatus(PaymentStatus.CREATED);
            entity.setAmount(BigDecimal.valueOf(20L));
            entity.setCorrelationId(paymentId);
            entity.setRequestedAt(LocalDateTime.now());
            paymentRepository.persistAndFlush(entity);

            paymentRepository.updateToProcessed(paymentId, ProcessorType.FALLBACK);
        }

        //
        {
            UUID paymentId = UUID.randomUUID();

            PaymentEntity entity = new PaymentEntity();
            entity.setStatus(PaymentStatus.CREATED);
            entity.setAmount(BigDecimal.valueOf(30L));
            entity.setCorrelationId(paymentId);
            entity.setRequestedAt(LocalDateTime.now());
            paymentRepository.persistAndFlush(entity);

            paymentRepository.updateToProcessed(paymentId, ProcessorType.FALLBACK);
        }
//        paymentRepository.updateToProcessed(paymentId, ProcessorType.DEFAULT);
        paymentRepository.listAll().forEach((p) -> Log.infov("found {0}", p.toString()));
        paymentRepository.flush();
    }

}
