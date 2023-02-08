package com.farhad.example.reactor;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static reactor.core.publisher.Mono.fromRunnable;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.test.StepVerifier;

public class CachePublishers {

    @Test
    public void uncachedFlux() {
        AtomicInteger count = new AtomicInteger();

        Mono<Integer> uncachedMono = Mono.just(1).doOnNext(i -> count.incrementAndGet());

        StepVerifier.create(uncachedMono)
                    .expectNextCount(1)
                    .verifyComplete();

        StepVerifier.create(uncachedMono)
                    .expectNextCount(1)
                    .verifyComplete();

        StepVerifier.create(uncachedMono)
                    .expectNextCount(1)
                    .verifyComplete();

        assertThat(count.get()).isEqualTo(3);

    }

    @Test
    public void cachedFlux() {
        AtomicInteger count = new AtomicInteger();

        Mono<Integer> uncachedMono = Mono.just(1).doOnNext(i -> count.incrementAndGet()).cache();

        StepVerifier.create(uncachedMono)
                    .expectNextCount(1)
                    .verifyComplete();

        StepVerifier.create(uncachedMono)
                    .expectNextCount(1)
                    .verifyComplete();

        StepVerifier.create(uncachedMono)
                    .expectNextCount(1)
                    .verifyComplete();

        assertThat(count.get()).isEqualTo(1);

    }

}
