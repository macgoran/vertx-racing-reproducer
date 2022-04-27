package reproducer;

import java.util.function.Supplier;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public class ProblemReproducerRunner extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.runOnContext(v -> runAllCases());
        startPromise.complete();
    }

    private void runAllCases() {
        Future.<Void>succeededFuture()
                .compose(this::run_SucceededFuture_Case)
                .compose(this::run_ExecuteBlocking_Case)
                .compose(this::run_Mapped_ExecuteBlocking_Case)
                .compose(this::run_OrderedFalse_ExecuteBlocking_Case)
                .compose(this::run_Mapped_OrderedFalse_ExecuteBlocking_Case)
                .compose(this::run_After_RunOnContext_Case)
                .compose(this::run_After_SetTimer_Case)
                .onComplete(x -> exit());
    }

    private Future<Void> run_SucceededFuture_Case(Void v) {
        Supplier<Future<Object>> futureSupplier = () -> {
            return Future.succeededFuture("result");
        };
        return new ProblemReproducerLoop("succeededFuture()", 100_000, futureSupplier).run();
    }

    private Future<Void> run_ExecuteBlocking_Case(Void v) {
        Supplier<Future<Object>> futureSupplier = () -> {
            return vertx
                    .executeBlocking(resultPromise -> {
                        resultPromise.complete("result");
                    });
        };
        return new ProblemReproducerLoop("executeBlocking()", 100_000, futureSupplier).run();
    }

    private Future<Void> run_Mapped_ExecuteBlocking_Case(Void v) {
        Supplier<Future<Object>> futureSupplier = () -> {
            return vertx
                    .executeBlocking(resultPromise -> {
                        resultPromise.complete("result");
                    })
                    .map(result -> result);
        };
        return new ProblemReproducerLoop("Mapped executeBlocking()", 100_000, futureSupplier).run();
    }

    private Future<Void> run_OrderedFalse_ExecuteBlocking_Case(Void v) {
        Supplier<Future<Object>> futureSupplier = () -> {
            return vertx
                    .executeBlocking(resultPromise -> {
                        resultPromise.complete("result");
                    }, false);
        };
        return new ProblemReproducerLoop("ordered=false executeBlocking()", 100_000, futureSupplier).run();
    }

    private Future<Void> run_Mapped_OrderedFalse_ExecuteBlocking_Case(Void v) {
        Supplier<Future<Object>> futureSupplier = () -> {
            return vertx
                    .executeBlocking(resultPromise -> {
                        resultPromise.complete("result");
                    }, false)
                    .map(result -> result);
        };
        return new ProblemReproducerLoop("Mapped ordered=false executeBlocking()", 100_000, futureSupplier).run();
    }

    private Future<Void> run_After_RunOnContext_Case(Void v) {
        Supplier<Future<Object>> futureSupplier = () -> {
            Promise<Object> resultPromise = Promise.promise();
            vertx.runOnContext(x -> resultPromise.complete("result"));
            return resultPromise.future();
        };
        return new ProblemReproducerLoop("After runOnContext()", 100_000, futureSupplier).run();
    }

    private Future<Void> run_After_SetTimer_Case(Void v) {
        Supplier<Future<Object>> futureSupplier = () -> {
            Promise<Object> resultPromise = Promise.promise();
            vertx.setTimer(1L, t -> resultPromise.complete("result"));
            return resultPromise.future();
        };
        String tag = "After setTimer(1L)";
        System.out.println("Running for future supplier: \"" + tag + "\" ... (done in 15-20 seconds) ...");
        return new ProblemReproducerLoop(tag, 1000, futureSupplier).run();
    }

    private void exit() {
        vertx.close()
                .onFailure(x -> System.exit(1))
                .onSuccess(x -> System.exit(0));
    }

}
