package com.farhad.example.reactor.canceling;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

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

}
