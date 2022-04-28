# vertx-racing-reproducer

    $ ./gradlew reproducer
    
    > Task :reproducer
    Future supplier: "succeededFuture()":  num runs: 100000  success: 100000  problems: 0
    Future supplier: "executeBlocking()":  num runs: 100000  success: 99983  problems: 17
    Future supplier: "Mapped executeBlocking()":  num runs: 100000  success: 100000  problems: 0
    Future supplier: "ordered=false executeBlocking()":  num runs: 100000  success: 99994  problems: 6
    Future supplier: "Mapped ordered=false executeBlocking()":  num runs: 100000  success: 100000  problems: 0
    Future supplier: "After runOnContext()":  num runs: 100000  success: 100000  problems: 0
    Running for future supplier: "After setTimer(1L)" ... (done in 15-20 seconds) ...
    Future supplier: "After setTimer(1L)":  num runs: 1000  success: 1000  problems: 0
    
    BUILD SUCCESSFUL in 22s
    2 actionable tasks: 1 executed, 1 up-to-date
