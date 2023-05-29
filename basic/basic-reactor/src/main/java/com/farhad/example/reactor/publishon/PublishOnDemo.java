package com.farhad.example.reactor.publishon;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class PublishOnDemo {

    public static void main(String[] args) throws InterruptedException {
        
        Flux.range(1, 10)
            .map(i -> {
                System.out.printf("[%s] Maping %s\n",Thread.currentThread().getName(), i );
                return i* 2 ;
            })
            .publishOn(Schedulers.parallel())
            .filter(i -> {
                System.out.printf("[%s] Filtering %s\n",Thread.currentThread().getName(), i );
                return i >= 10;

            })
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();

            Thread.sleep(2000);
    }
    
}
