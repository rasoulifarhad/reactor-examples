package com.farhad.example.reactor;
import reactor.core.publisher.Flux;

public class MockeService {
    
    public Flux<Integer> getValues() {
        return Flux.just(1,2,3);
    }
}
