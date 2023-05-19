package com.farhad.example.reactor.threading;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
/**
 * subscribeOn operator changes where the subscribe method is executed. And since the subscribe signal flows upward, it 
 * directly influences where the source Flux subscribes and starts generating data.
 * 
 * As a consequence, it can seem to act on the parts of the reactive chain of operators upward and downward (as long as 
 * there is no publishOn thrown in the mix)
 */
public class SubscribeOnDemo {
    
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
        // imagine that we havent acess to this code
        final static Flux<String> fetchUrls(List<String> urls) {
            return Flux 
                    .fromIterable(urls)
                    .map(url -> get(url));
        }
    }


    static class WithoutSubscribeOn {
        public static void main(String[] args) {
            List<String> firstList=  Arrays.asList("A", "B", "C");
            List<String> secondList=  Arrays.asList("D", "E", "F");
            BlockingWebClient
                .fetchUrls(firstList)
                .subscribe(body -> log.info("From first list, got {}", body));

            BlockingWebClient
                .fetchUrls(secondList)
                .subscribe(body -> log.info("From second list, got {}", body));
        }
    }

    /**
     * The subscribe calls are still running on the main thread, but they propagate a subscribe signal to their source, 
     * subscribeOn. In turn subscribeOn propagates that same signal to its own source from fetchUrls, but on a 
     * boundedElastic Worker.
     * 
     * In the Flux sequence returned by fetchUrls, the map is subscribed on the boundedElastic worker thread, and so is 
     * the range. The range starts generating data, still on the boundedElastic worker thread.
     * 
     * This continues down the data path, each subscriber executing onNext on its source thread, the boundedElastic one.
     * 
     * At last, the lambdas configured in the subscribe(...) call are also executed on the boundedElastic thread.
     */
    static class WithSubscribeOn {
        public static void main(String[] args) throws InterruptedException {
            List<String> firstList=  Arrays.asList("A", "B", "C");
            List<String> secondList=  Arrays.asList("D", "E", "F");
            BlockingWebClient
                .fetchUrls(firstList)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(body -> log.info("From first list, got {}", body));

            BlockingWebClient
                .fetchUrls(secondList)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(body -> log.info("From second list, got {}", body));

            Thread.sleep(5000);
        }
    }

}
