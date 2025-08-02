package mutiny.study;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;

/**
 * on
 */
public class StudyMutinyTest {

    static class Entity {

        private final Integer id;
        private final String precessedBy;

        private Entity(Integer id, String precessedBy) {
            this.id = id;
            this.precessedBy = precessedBy;
        }

        static Entity byId(Integer id) {
            return new Entity(id, null);
        }

        Entity withPrecessedBy(String processedBy) {
            return new Entity(this.id, processedBy);
        }

        @Override
        public String toString() {
            return "Entity{" +
                    "id=" + id +
                    ", precessedBy=" + precessedBy +
                    '}';
        }
    }

    class MyException extends RuntimeException {
        MyException(String message) {
            super(message);
        }
    }

    @Test
    void testFailure() {
        Uni.createFrom()
                .item(Entity.byId(42))
                .onItem().transformToUni(this::willFail)
                .map(entity -> 20)
                .onFailure().recoverWithNull()
                .onFailure().invoke(() -> Log.error("on failure"))
                .subscribe()
                .with(
                        v -> Log.infov("success {0}", v),
                        t -> Log.infov("failure {0}", t)
                );
    }


    private Uni<Entity> willFail(Entity entity) {
        return Uni.createFrom().failure(new MyException("test"));
    }


    @Test
    void testParallel() throws InterruptedException {
        /// TODO assert parallels
        /// TODO second
        var items = Arrays.asList(Entity.byId(1), Entity.byId(2), Entity.byId(3));

        var multi = Multi.createFrom()
                .items(items.toArray(Entity[]::new))

                /// transform synchronous
                //                .onItem().transformToUniAndConcatenate(this::processPayment);/

                // transform asynchronous set concurrency to 2
                .onItem().transformToUni(this::processPayment).merge(2);
        /// transform asynchronous
        //.onItem().transformToUniAndMerge(this::processPayment);

        multi
                .subscribe()
                .with(
                        v -> Log.infov("multi subscribe success receive: {0}", v),
                        t -> Log.error("multi subscribe fail receive:", t));

        Thread.sleep(Duration.ofSeconds(8));
    }

    private Uni<Entity> processPayment(Entity item) {
        Log.infov("processPayment item: {0}", item);
        return Uni.createFrom().item(item)
                .onItem().transformToUni(this::processorOne)
                .onFailure().retry().indefinitely()
                .onFailure().recoverWithUni(() -> processorTwo(item));
    }

    Integer processOneExecutionCount = 0;

    private Uni<Entity> processorOne(Entity item) {
        processOneExecutionCount++;

        if (processOneExecutionCount == 2)
            return Uni.createFrom().failure(new MyException("my exception"));

        return Uni.createFrom().item(item)
                .onItem().delayIt().by(Duration.ofSeconds(3))
                .onItem().transform(i -> i.withPrecessedBy("ONE"));
    }


    private Uni<Entity> processorTwo(Entity item) {
        Log.infov("on processorTwo {0}", item);
        return Uni.createFrom().item(item)
                .onItem().transform(i -> i.withPrecessedBy("TWO"));
    }


}
