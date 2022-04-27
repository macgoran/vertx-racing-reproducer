package reproducer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import io.vertx.core.Future;
import io.vertx.core.Promise;

public class ProblemReproducer {

    private int value;

    public Future<Void> run(Supplier<Future<Object>> futureSupplier, AtomicInteger success, AtomicInteger problem) {

        Promise<Void> completionPromise = Promise.promise();

        futureSupplier.get()
                .onSuccess(x -> {
                    value = 5555;
                })
                .onSuccess(x -> {
                    if (value == 5555) {
                        success.getAndIncrement(); // This should always happen.
                    } else {
                        problem.getAndIncrement(); // This should never happen.
                    }
                })
                .onComplete(x -> {
                    completionPromise.complete();
                });

        return completionPromise.future();

    }

}
