package com.farhad.example.reactor.cold;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class ColdPublisherDemo {
    
    public static void main(String[] args) throws InterruptedException {
        
        Flux<Long> clockTicks = Flux.interval(Duration.ofSeconds(1));

        clockTicks.subscribe(tick ->  log.info("clock01 {} s", tick));

        Thread.sleep(2000);

        clockTicks.subscribe(tick -> log.info("\tclock02 {} s", tick) );

        Thread.sleep(10000);
    }

}
