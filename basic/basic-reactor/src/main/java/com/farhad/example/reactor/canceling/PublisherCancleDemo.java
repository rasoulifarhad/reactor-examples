package com.farhad.example.reactor.canceling;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

@Slf4j
public class PublisherCancleDemo {
    
    static class TakeUntilDemo {
        public static void main(String[] args) {
            Flux<Integer> intFlux =  Flux.range(1, 10);
            List<Integer> intResult = new ArrayList<>();
            intFlux
                .takeUntil(i -> i == 8)
                .subscribe(intResult::add);
            log.info("intResult: {}", intResult);
        }
    }

    static class TakeWhileDemo {

        public static void main(String[] args) {
            Flux<Integer> intFlux = Flux.range(1, 10);
            List<Integer> intResult = new ArrayList<>();
            intFlux
                .takeWhile(i -> i<= 8)
                .subscribe(intResult::add);
            log.info("{}", intResult);
        }
    }

    static class TakeCountDemo {

        public static void main(String[] args) {
            Flux<Integer> intFlux = Flux.range(1, Integer.MAX_VALUE);
            List<Integer> intResult = new ArrayList<>();
            intFlux
                .take(10)
                .subscribe(intResult::add);
            log.info("{}", intResult);
        }
    }

    static class TakeTimeoutDemo {

        public static void main(String[] args) throws InterruptedException {
            Flux<Integer> intFlux = 
                        Flux
                            .interval(Duration.ZERO, Duration.ofSeconds(2))
                            .map(i -> i.intValue() + 10)
                            .take(5);
            List<Integer> intResult = new ArrayList<>();
            intFlux
                .take(Duration.ofSeconds(3))
                .subscribe(intResult::add);

            Thread.sleep(10000);
            log.info("intResult: {}", intResult);
        }
    }

    static class DisposeDemo {

        public static void main(String[] args) throws InterruptedException {
            Flux<Integer> intFlux = 
                            Flux.range(1, 10)
                                .delayElements  (Duration.ofSeconds(1));

            AtomicInteger count = new AtomicInteger(0);
            Disposable disposable =  intFlux.subscribe(i -> {
                                            log.info("Received: {}", i);
                                            count.incrementAndGet();
                                    },e -> log.error("Error: {}", e.getMessage()));
            Thread.sleep(5000);
            log.info("Disposing publisher");
            disposable.dispose();
            if (disposable.isDisposed()) {
                log.info("Disposed");
            }

            log.info("Count: {}", count.get());
        }
    }

    static class DisposeDoFinalyDemo {

        public static void main(String[] args) throws InterruptedException {
            List<Integer> intResult = new ArrayList<>();
            Flux<Integer> intFlux = 
                            Flux.range(1, 10)
                                .delayElements  (Duration.ofSeconds(1))
                                .doOnCancel(() -> log.info("Canceled!!"))
                                .doFinally(signaltype -> {
                                    if(signaltype == SignalType.CANCEL) {
                                        log.info("Complated with cancelation!");
                                    } else {
                                        log.info("Complated with {} signal!", signaltype);
                                    }
                                })
                                .map(i -> ThreadLocalRandom.current().nextInt(1, 1001))
                                .doOnNext(intResult::add);

            Disposable subscription =  intFlux.subscribe();
            Thread.sleep(5000);
            log.info("Disposing subscription!");
            subscription.dispose();
            if (subscription.isDisposed()) {
                log.info("subscription Disposed!");
            }
            log.info("Result list is: {}", intResult);

        }
    }

}
