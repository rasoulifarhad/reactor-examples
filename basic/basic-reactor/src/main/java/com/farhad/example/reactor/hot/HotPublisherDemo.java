package com.farhad.example.reactor.hot;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class HotPublisherDemo {
    
    public static void main(String[] args) throws InterruptedException {
        Flux<Long> coldTicks  = Flux.interval(Duration.ofSeconds(1));
        Flux<Long> clockTicks = coldTicks.share();

        clockTicks.subscribe(tick -> log.info("clock01 {} s", tick) );

        Thread.sleep(2000);

        clockTicks.subscribe(tick -> log.info("\tclock02 {} s", tick) );

        Thread.sleep(20000);
    }
}
