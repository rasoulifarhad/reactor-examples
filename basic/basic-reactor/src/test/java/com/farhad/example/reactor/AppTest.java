package com.farhad.example.reactor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j 
public class AppTest {
    
    @Test
    public void repeat() {

        AtomicInteger counter = new AtomicInteger(0);

        Flux<Integer> repeated = Mono
                                    .fromCallable(counter::incrementAndGet)
                                    .repeat(9);


        log.info("Repeat: ...");

        StepVerifier
                .create(repeated.doOnNext(i -> log.info("{}",i)))
                .expectNext(1,2,3,4,5,6,7,8,9,10)
                .verifyComplete();

    }


    @Test
    public void generate_create_push__programmatically() {

        AtomicInteger counter = new AtomicInteger(1);
        Flux<Integer> generateFlux = Flux.generate(sink -> {
                            if (counter.get() > 5  ) {
                                sink.complete();
                            } else {
                                sink.next(counter.getAndIncrement());
                            }
        } );

        Flux<Integer> createFlux = Flux.create(sink ->{
                for (int i = 1; i <= 5; i++) {
                    sink.next(i);
                }
                sink.complete();;
        } );

        Flux<Integer> pushFlux = Flux.push(sink ->{
            for (int i = 1; i <= 5; i++) {
                sink.next(i);
            }
            sink.complete();;
        } );

        StepVerifier
                              .create(generateFlux)
                              .expectNext(1,2,3,4,5)
                              .verifyComplete() ;

        StepVerifier
                              .create(createFlux)
                              .expectNext(1,2,3,4,5)
                              .verifyComplete() ;

        StepVerifier
                              .create(pushFlux)
                              .expectNext(1,2,3,4,5)
                              .verifyComplete() ;
                              
    }

    @Test
    public void futureFlux() {

        AtomicInteger counter = new AtomicInteger(1);

        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
               log.info("Increment counter");
               return counter.incrementAndGet();
        });

        Mono<Integer> mono =  Mono.fromFuture(future);

        StepVerifier
               .create(mono)
               .expectNext(1)
               .verifyComplete();

    }
}
