package com.farhad.example.reactor.threading;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
                long sleepTime  = (long) (Math.random() * 100);
                Thread.sleep(sleepTime);
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

        final static Flux<String> betterfetchUrls(List<String> urls) {
            return Flux 
                    .fromIterable(urls)
                    .flatMap(url -> 
                            Mono
                                // wrap blocking  call in mono
                                .fromCallable(() -> get(url))
                                .subscribeOn(Schedulers.boundedElastic())
                   );
        }
    }


    static class WithoutSubscribeOn {
        public static void main(String[] args) {
            List<String> firstList=  Arrays.asList("A", "B", "C");
            List<String> secondList=  Arrays.asList("D", "E", "F");
            BlockingWebClient
                .fetchUrls(firstList)
                // .log()
                .subscribe(body -> log.info("From first list, got {}", body));

            BlockingWebClient
                .fetchUrls(secondList)
                // .log()
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
     * 
     * It is important to distinguish the act of subscribing and the lambda passed to the subscribe() method. This method 
     * subscribes to its source Flux, but the lambda are executed at the end of processing, when the data has flown through 
     * all the steps (including steps that hop to another thread),.
     * 
     * So the Thread on which the lambda is executed might be different from the subscription Thread , ie. the thread on 
     * which the subscribe method is called.
     */
    static class WithSubscribeOn {
        public static void main(String[] args) throws InterruptedException {
            List<String> firstList=  Arrays.asList("A", "B", "C");
            List<String> secondList=  Arrays.asList("D", "E", "F");
            BlockingWebClient
                .fetchUrls(firstList)
                .subscribeOn(Schedulers.boundedElastic())
                // .log()
                .subscribe(body -> log.info("From first list, got {}", body));

            BlockingWebClient
                .fetchUrls(secondList)
                .subscribeOn(Schedulers.boundedElastic())
                // .log()
                .subscribe(body -> log.info("From second list, got {}", body));

            Thread.sleep(5000);
        }
    }

    static class WithBetterfetchUrls {
        public static void main(String[] args) throws InterruptedException {
            List<String> firstList=  Arrays.asList("A", "B", "C");
            List<String> secondList=  Arrays.asList("D", "E", "F");
            BlockingWebClient
                .betterfetchUrls(firstList)
                // .log()
                .subscribe(body -> log.info("From first list, got {}", body));

            BlockingWebClient
                .betterfetchUrls(secondList)
                // .log()
                .subscribe(body -> log.info("From second list, got {}", body));

            Thread.sleep(5000);
        }
    }

    static class WithBetterfetchUrlsAndSubscribeOn {
        public static void main(String[] args) throws InterruptedException {
            List<String> firstList=  Arrays.asList("A", "B", "C");
            List<String> secondList=  Arrays.asList("D", "E", "F");
            BlockingWebClient
                .betterfetchUrls(firstList)
                .subscribeOn(Schedulers.boundedElastic())
                // .log()
                .subscribe(body -> log.info("From first list, got {}", body));

            BlockingWebClient
                .betterfetchUrls(secondList)
                .subscribeOn(Schedulers.boundedElastic())
                // .log()
                .subscribe(body -> log.info("From second list, got {}", body));

            Thread.sleep(10000);
        }
    }

    /**
     * By default, the chain is executed by the thread where the .subscribe() was invoked.
     */
    static class ChainExcutionBlockMainThread {
        public static void main(String[] args) {
            Flux.just(1, 2, 3)
                .map(i -> {
                    System.out.printf("Thread [%s] Incrementing  %s\n", Thread.currentThread().getName(), i);
                    return i + 1;
                })
                .subscribe(i -> {
                    System.out.printf("Thread [%s] Got %s \n", Thread.currentThread().getName(), i);
                });
            
            System.out.printf("Thread [%s] ,After the Flux!\n", Thread.currentThread().getName());
        }
    }

    static class ChainExcutionNonBlockMainThread {
        public static void main(String[] args) throws InterruptedException {
            Flux.just(1, 2, 3)
                .map(i -> {
                    System.out.printf("Thread [%s] Incrementing  %s\n", Thread.currentThread().getName(), i);
                    return i + 1;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(i -> {
                    System.out.printf("Thread [%s] Got %s \n", Thread.currentThread().getName(), i);
                });
            
            System.out.printf("Thread [%s] ,After the Flux!\n", Thread.currentThread().getName());

            Thread.sleep(2000);
        }
    }

}
