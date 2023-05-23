package com.farhad.example.reactor.threading;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * - Here subscribe is called on the main thread, but subscription is rapidly switched to the elastic scheduler due to the subscribeOn 
 *   immediately above.
 * - All the operators above it are also subscribed on elastic, from bottom to top.
 * - just emits its value on the elastic scheduler.
 * - the first doOnNext receives that value on the same thread
 * - then on the top to bottom data path, we encounter the publishOn: data from doOnNext is propagated downstream on the boundedElastic 
 *   scheduler.
 * - the second doOnNext receives its data on boundedElastic
 * - delayElements is a time operator, so by default it publishes data on the Schedulers.parallel() scheduler.
 * - on the data path, subscribeOn does nothing but propagating signal on the same thread.
 * - on the data path, the lambda(s) passed to subscribe(...) are executed on the thread in which data signals are received,(Schedulers.parallel())
 */
@Slf4j
public class MixPublishOnSubscribeOnDemo {
    public static void main(String[] args) throws InterruptedException {
        
        Flux.just("Hello")
            .doOnNext(t -> log.info("Just"))
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(t -> log.info("Publish"))
            .delayElements(Duration.ofMillis(500))
            .subscribeOn(Schedulers.elastic())
            .subscribe(t -> log.info("Delayed"))
            ;

            Thread.sleep(2000);
    }
}
