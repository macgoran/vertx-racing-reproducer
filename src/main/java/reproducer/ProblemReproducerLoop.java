package reproducer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class ProblemReproducerLoop {

    private String tag;
    private int numRuns;
    private Supplier<Future<Object>> futureSupplier;

    private Vertx vertx;
    private AtomicInteger success = new AtomicInteger();
    private AtomicInteger problem = new AtomicInteger();

    public ProblemReproducerLoop(String tag, int numRuns, Supplier<Future<Object>> futureSupplier) {
        this.tag = tag;
        this.numRuns = numRuns;
        this.futureSupplier = futureSupplier;
        vertx = Vertx.currentContext().owner();
    }

    public Future<Void> run() {
        Promise<Void> completionPromise = Promise.promise();
        int runIndex = 1;
        run(runIndex, completionPromise);
        return completionPromise.future();
    }

    private void run(int runIndex, Promise<Void> completionPromise) {
        ProblemReproducer problemReproducer = new ProblemReproducer();
        problemReproducer.run(futureSupplier, success, problem)
                .onComplete(x -> {
                    if (runIndex < numRuns) {
                        vertx.runOnContext(v -> {
                            int nextRunIndex = runIndex + 1;
                            run(nextRunIndex, completionPromise);
                        });
                    } else {
                        System.out.println("Future supplier: \"" + tag + "\":" +
                                "  num runs: " + numRuns +
                                "  success: " + success.get() +
                                "  problems: " + problem.get());
                        completionPromise.complete();
                    }
                });
    }

}
