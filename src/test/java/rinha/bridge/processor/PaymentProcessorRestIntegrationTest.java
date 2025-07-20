package rinha.bridge.processor;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@QuarkusTest
public class PaymentProcessorRestIntegrationTest {

    @RestClient
    PaymentProcessorClient client;

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

}
