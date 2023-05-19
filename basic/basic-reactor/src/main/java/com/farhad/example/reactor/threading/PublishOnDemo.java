package com.farhad.example.reactor.threading;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class PublishOnDemo {
    
    static class BlockingWebClient {
        public static String get(String url) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
            return url;
        } 
    }
    static class WithoutPublishOn {

        public static void main(String[] args) {
            List<String> firstList=  Arrays.asList("A", "B", "C");
            List<String> secondList=  Arrays.asList("D", "E", "F");
            Flux
                .fromIterable(firstList)
                .map(url -> BlockingWebClient.get(url))
                .subscribe(body -> log.info("From first list, got {}", body));

            Flux
                .fromIterable(secondList)
                .map(url -> BlockingWebClient.get(url))
                .subscribe(body -> log.info("From second list, got {}", body));
        }
    }

    static class WithPublishOn {

        public static void main(String[] args) throws InterruptedException {
            List<String> firstList=  Arrays.asList("A", "B", "C");
            List<String> secondList=  Arrays.asList("D", "E", "F");
            Flux
                .fromIterable(firstList)
                .publishOn(Schedulers.boundedElastic())
                .map(url -> BlockingWebClient.get(url))
                .subscribe(body -> log.info("From first list, got {}", body));

            Flux
                .fromIterable(secondList)
                .publishOn(Schedulers.boundedElastic())
                .map(url -> BlockingWebClient.get(url))
                .subscribe(body -> log.info("From second list, got {}", body));

        Thread.sleep(5000);
        }

    }

}
