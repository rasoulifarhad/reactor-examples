package com.farhad.example.reactor;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class CreatePublishers {
    
    private static final String VALUE = "test";

    @Test
    public void createMono() {

        Mono<String> mono =  Mono.just(VALUE);

        StepVerifier.create(mono)    
                    .expectNext(VALUE)
                    .verifyComplete();
    }

    @Test
    public void createFlux() {

        Flux<String> flux =  Flux.just(VALUE,VALUE,VALUE);

        StepVerifier.create(flux)    
                    .expectNext(VALUE)
                    .expectNext(VALUE)
                    .expectNext(VALUE)
                    .verifyComplete();

        StepVerifier.create(flux)    
                    .expectNextCount(1)
                    .expectNext(VALUE)
                    .assertNext(value -> assertThat(value).isEqualTo(VALUE))
                    .verifyComplete();

    }

    @Test
    public void justOrEmpty() {

        Mono<String> nonNullValue = Mono.justOrEmpty(VALUE);

        StepVerifier.create(nonNullValue)
                    .expectNext(VALUE)
                    .verifyComplete();
                    
        Optional<String> optional = Optional.of(VALUE);
        Mono<String> optionalWithValue = Mono.justOrEmpty(optional);

        StepVerifier.create(optionalWithValue)
                    .expectNext(VALUE)
                    .verifyComplete();

        Mono<String> emptyOptional = Mono.justOrEmpty(Optional.empty());

        StepVerifier.create(emptyOptional)
                    .verifyComplete();

        Mono<String> nullValue = Mono.justOrEmpty(null);

        StepVerifier.create(nullValue)
                    .verifyComplete();

    }

    @Test
    public void fromIterable() {

        List<String>  list = Arrays.asList(VALUE,VALUE,VALUE);

        Flux<String> fluxFromList = Flux.fromIterable(list);

        StepVerifier.create(fluxFromList)
                    .expectNext(VALUE)
                    .expectNext(VALUE, VALUE)
                    .verifyComplete();

        Mono<List<String>> monoOfList = fluxFromList.collectList();

        StepVerifier.create(monoOfList)
                    .assertNext(l -> assertThat(l).hasSize(3));
    }


}
