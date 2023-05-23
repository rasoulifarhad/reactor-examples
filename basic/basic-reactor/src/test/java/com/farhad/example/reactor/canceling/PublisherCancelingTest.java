package com.farhad.example.reactor.canceling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
public class PublisherCancelingTest {
    
    @Test
    public void takeUntilTest() {
        // Given
        Flux<Integer> intFlux =  Flux.range(1, 10);
        List<Integer> intResult = new ArrayList<>();

        // When
        intFlux
            .takeUntil(i -> i == 8)
            .subscribe(intResult::add);

        // Then
        assertThat(intResult).containsExactly(1,2,3,4,5,6,7,8);
    }

    @Test
    public void takeWhileTest() {

        //Given
        Flux<Integer> intFlux = Flux.range(1, 10);
        List<Integer> intResult = new ArrayList<>();

        //When
        intFlux
            .takeWhile(i -> i<= 8)
            .subscribe(intResult::add);

        //Then
        assertThat(intResult).containsExactly(1,2,3,4,5,6,7,8);
    }

    @Test
    public void takeCountTest() {

        //Given
        Flux<Integer> intFlux = Flux.range(1, Integer.MAX_VALUE);
        List<Integer> intResult = new ArrayList<>();

        //When
        intFlux
            .take(10)
            .subscribe(intResult::add);

        //Then
        assertThat(intResult).containsExactly(1,2,3,4,5,6,7,8,9,10);
    }

    @Test
    public void takeTimeoutTest() {

        //Given
        Flux<Integer> intFlux = 
                        Flux
                            .interval(Duration.ZERO, Duration.ofSeconds(2))
                            .map(i -> i.intValue() + 10)
                            .take(5);
        //When
        Flux<Integer> intFluxTimeout =  intFlux
                                            .take(Duration.ofSeconds(3));

        //Then
        StepVerifier.create(intFluxTimeout)
                    .expectNext(10, 11)
                    .expectComplete()
                    .verify();    
    }

    @Test
    public void disposeTest() throws InterruptedException {

        //Given
        Flux<Integer> intFlux = 
                    Flux.range(1, 10)
                        .delayElements(Duration.ofSeconds(1));

        //When
        AtomicInteger count = new AtomicInteger(0);
        Disposable disposable =  intFlux.subscribe(i -> {
                                            log.info("Received: {}", i);
                                            count.incrementAndGet();
                                    },e -> log.error("Error: {}", e.getMessage()));
        Thread.sleep(5000);
        disposable.dispose();;
        
        //Then
        assertEquals(4, count.get());
    }

}
