package com.farhad.example.reactor;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ConvertingPublishers {
    
    @Test
    public void singleOrLast() {

        Flux<Integer> flux = Flux.range(1, 3);

        Mono<Integer> next = flux.next();
        Mono<Integer> last = flux.last();

        StepVerifier.create(next)
                    .expectNext(1)
                    .verifyComplete();

        StepVerifier.create(last)
                    .expectNext(3)
                    .verifyComplete();

    }

    @Test
    public void monoRepeat() {

        Flux<Integer> flux = Mono.just(1).repeat(3);


        StepVerifier.create(flux)
                    .expectNextCount(4)
                    .verifyComplete();


    }

    @Test
    public void monoZip() {

        Mono<Integer> mono1 = Mono.just(1);
        Mono<Integer> mono2 = Mono.just(2);


        StepVerifier.create(mono1.zipWith(mono2))
                    .assertNext(tuple -> {
                        assertThat(tuple.getT1()).isEqualTo(1);
                        assertThat(tuple.getT2()).isEqualTo(2);
                    })
                    .verifyComplete();


    }

    @Test
    public void monoZipWithCombinator() {

        Mono<Integer> mono1 = Mono.just(1);
        Mono<Integer> mono2 = Mono.just(2);


        StepVerifier.create(mono1.zipWith(mono2,(a,b) -> a+b))
                     .expectNext(3)   
                    .verifyComplete();


    }

    @Test
    public void fluxFullZip() {

        Flux<Integer> flux1 = Flux.just(1,2);
        Flux<Integer> flux2 = Flux.just(3,4,5);


        StepVerifier.create(flux1.zipWith(flux2))
                    .expectNextCount(2)
                    .verifyComplete();


    }

    @Test
    public void fluxZipMono() {

        Flux<Integer> flux = Flux.just(1,2);
        Mono<Integer> mono = Mono.just(3);



        StepVerifier.create(flux.zipWith(mono))
                    .expectNextCount(1)
                    .verifyComplete();


        StepVerifier.create(flux.zipWith(mono.repeat()))
                    .expectNextCount(2)
                    .verifyComplete();

    }

    @Test
    public void fluxFullZipWithCombinator() {

        Flux<Integer> flux1 = Flux.just(1,2);
        Flux<Integer> flux2 = Flux.just(3,4,5);



        StepVerifier.create(flux1.zipWith(flux2,(a,b) -> a+b))
                    .expectNext(4,6)
                    .verifyComplete();


    }

    @Test
    @DisplayName("Zip more than 2 publishers into one")
    void zipMoreThanTwo()
    {
        Flux<Integer> flux1 = Flux.just(1, 2, 3);
        Flux<Integer> flux2 = Flux.just(4, 5);
        Mono<String> mono = Mono.just("1");

        StepVerifier.create(Flux.zip(flux1, flux2, mono))
                .assertNext(tuple -> {
                    assertThat(tuple.getT1()).isEqualTo(1);
                    assertThat(tuple.getT2()).isEqualTo(4);
                    assertThat(tuple.getT3()).isEqualTo("1");
                })
                .verifyComplete();

        StepVerifier.create(Flux.zip(flux1, flux2, mono.repeat()))
                .expectNextCount(2)
                .verifyComplete();
    }
}
