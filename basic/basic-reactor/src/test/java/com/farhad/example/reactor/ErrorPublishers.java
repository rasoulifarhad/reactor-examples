package com.farhad.example.reactor;


import static java.lang.String.format;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class ErrorPublishers {
    

    @Test
    public void onErrorReturn() {

        Flux<Integer> onErrorReturnFlux = operationWithError().onErrorReturn(1);

        StepVerifier.create(onErrorReturnFlux)
                    .expectNext(1)
                    .verifyComplete();

        Flux<Integer> onErrorReturnForClass = operationWithError().onErrorReturn(PublisherException.class  , 1);

        StepVerifier.create(onErrorReturnForClass)
                    .expectNext(1)
                    .verifyComplete();

        Flux<Integer> onErrorReturnForPredicate = operationWithError().onErrorReturn(throwable -> throwable.getMessage().contains("Error"), 1);

                    StepVerifier.create(onErrorReturnForPredicate)
                            .expectNext(1)
                            .verifyComplete();
    }

    @Test
    public void onErrorResume() {

        Flux<Integer> alternate = Flux.just(1);
        
        Flux<Integer>  onErrorResumeFlux = operationWithError().onErrorResume(throwable -> alternate);
        StepVerifier.create(onErrorResumeFlux)
                        .expectNext(1)
                        .verifyComplete();

        Flux<Integer> onErrorResumeFluxForClass = operationWithError().onErrorResume(PublisherException.class, throwable -> alternate);
        StepVerifier.create(onErrorResumeFluxForClass)
                        .expectNext(1)
                        .verifyComplete();

        Flux<Integer> onErrorResumeForPredicate = operationWithError().onErrorResume(throwable -> throwable.getMessage().contains("Error"), throwable -> alternate);
        StepVerifier.create(onErrorResumeForPredicate)
                        .expectNext(1)
                        .verifyComplete();
    }

    private Flux<Integer> operationWithError() {
        return Flux.error(new PublisherException("Error"));
    }

    private static class PublisherException extends RuntimeException {
        static final long serialVersionUID = 1L;

        public PublisherException(final String message)
        {
            super(message);
        }

    }
}
