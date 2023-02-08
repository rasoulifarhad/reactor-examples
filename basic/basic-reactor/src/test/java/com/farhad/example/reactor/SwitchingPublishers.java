package com.farhad.example.reactor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static reactor.core.publisher.Flux.defer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class SwitchingPublishers {
    

    @Test
    public void switchOnEmpty() {

        Mono<Integer> mono = Mono.<Integer>empty()
                                    .switchIfEmpty(Mono.just(1));

        StepVerifier.create(mono)
                    .expectNext(1)
                    .verifyComplete();

        Flux<Integer> flux = Flux.<Integer>empty()
                                    .switchIfEmpty(Mono.just(1));

        StepVerifier.create(flux)
                    .expectNext(1)
                    .verifyComplete();

        Mono<Integer> mono2 = Mono.<Integer>empty()
                                    .defaultIfEmpty(1);

        StepVerifier.create(mono2)
                    .expectNext(1)
                    .verifyComplete();

        Flux<Integer> flux2 = Flux.<Integer>empty()
                                    .defaultIfEmpty(1);

        StepVerifier.create(flux2)
                    .expectNext(1)
                    .verifyComplete();

    }

    @Test
    public void switchOnFirst() {

        Flux<Integer> switchOnFirstFlux =  Flux.just(1,2,3,4,5)
                                    .switchOnFirst((signal,flux) -> signal.get() == 2 ? flux : switchFlux(flux) )    ;

        StepVerifier.create(switchOnFirstFlux)
                    .expectNext(2,4,6,8)
                    .verifyComplete();

        Flux<Integer> noSwitchOnFirstFlux =  Flux.just(2,3,4,5)
                                    .switchOnFirst((signal,flux) -> signal.get() == 2 ? flux : switchFlux(flux) )    ;

        StepVerifier.create(noSwitchOnFirstFlux)
                    .expectNext(2,3,4,5)
                    .verifyComplete();

    }

    private Flux<Integer> switchFlux(Flux<Integer> flux) {

        return flux.map(i -> i *2 ).filter(i -> i < 10);
    }

    @Test
    public void switchIfEmptyDsfer() {
        
        MockeService mockeService = mock(MockeService.class);
        Flux<Integer> flux =Flux.just(1,2,3,4);
        Flux<Integer> switchWithDefer  = flux.switchIfEmpty(defer(() ->  mockeService.getValues() ));

        StepVerifier.create(switchWithDefer)
                    .expectNext(1,2,3,4)
                    .verifyComplete();

        verifyNoInteractions(mockeService);

    }
}
