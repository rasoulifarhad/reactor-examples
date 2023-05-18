package com.farhad.example.reactor.debugging;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class DebuggingCaveatsDemo {
    
    static class ImperativeDemo {

        public static void main(String[] args) throws Exception {

            imperative();
        
        }

        private static void imperative() throws Exception {

            ScheduledExecutorService executor = null;
            try {
                
                executor = Executors.newSingleThreadScheduledExecutor();

                int seconds = LocalTime.now().getSecond();
                List<Integer> source ;

                if( seconds % 2  == 0 )  {
                    source = IntStream.range(1, 11).boxed().collect(Collectors.toList());
                } else if ( seconds % 3 ==0 ) {
                    source = IntStream.range(1, 4).boxed().collect(Collectors.toList());
                } else {
                    source = Arrays.asList(1,2,3);
                }

                Future<Integer> resultFuture =  executor.submit(() -> source.get(5));

                resultFuture.get(5, TimeUnit.SECONDS);

            } finally  {
                log.info("Timeout get");
                executor.shutdown();
                log.info("End imperative");
            }
        }
    }

    static class ReactiveDemo {

        public static void main(String[] args) {
            reactive();    
        }

        private static void reactive() {

            int seconds = LocalTime.now().getSecond();

            Mono<Integer> source ;

            if( seconds % 2  == 0 )  {
                source = Flux.range(1, 11).elementAt(5);
            } else if ( seconds % 3 ==0 ) {
                source = Flux.range(1, 4).elementAt(5);
            } else {
                source = Flux.just(1,2,3).elementAt(5);
            }

            source.subscribeOn(Schedulers.parallel())
                        .block();
            
            source.block();
        }
    }

    static class ReactiveDemo2 {

        public static void main(String[] args) {
            reactive();    
        }

        private static void reactive() {

            int seconds = LocalTime.now().getSecond();

            Mono<Integer> source ;

            if( seconds % 2  == 0 )  {
                source = Flux.range(1, 11).elementAt(5);
            } else if ( seconds % 3 ==0 ) {
                source = Flux.range(1, 4).elementAt(5);
            } else {
                source = Flux.just(1,2,3).elementAt(5);
            }

            source.block();
        }
    }

    static class ReactiveDemoWithLog {

        public static void main(String[] args) {
            reactive();    
        }

        private static void reactive() {

            int seconds = LocalTime.now().getSecond();

            Mono<Integer> source ;

            if( seconds % 2  == 0 )  {
                source = Flux.range(1, 11)
                             .elementAt(5)
                             .log("Source A");
            } else if ( seconds % 3 ==0 ) {
                source = Flux.range(1, 4)
                             .elementAt(5)
                             .log("Source B");
            } else {
                source = Flux.just(1,2,3)
                             .elementAt(5)
                             .log("Source C");
            }

            source.block();
        }
    }

    /**
     * What does it do? It makes each operator instantiation (aka assembly) capture a stacktrace and keep it for later.
     * 
     * If an onError reaches one operator, it will attach that assembly stacktrace to the onError 's Throwable (as a 
     * suppressed Exception). As a result, when you see the stacktrace you'll get a more complete picture of both the 
     * runtime AND the assembly.
     * 
     * One drawback of using Hooks.onOperatorDebug() is that it does the assembly stacktrace capture for every single
     * operator used in the application. 
     */
    static class ReactiveDemoWithEnrichStackTraceWithDebugMode {

        public static void main(String[] args) {
            reactive();    
        }

        private static void reactive() {
            Hooks.onOperatorDebug();
            int seconds = LocalTime.now().getSecond();

            Mono<Integer> source ;

            if( seconds % 2  == 0 )  {
                source = Flux.range(1, 11)
                             .elementAt(5);
            } else if ( seconds % 3 ==0 ) {
                source = Flux.range(1, 4)
                             .elementAt(5);
            } else {
                source = Flux.just(1,2,3)
                             .elementAt(5);
            }

            source.block();
        }
    }

    /**
     * By using the checkpoint() operator, it is possible to activate the assembly trace capture only at that specific point in 
     * the codebase. You can even do entirely without the filling of a stacktrace if you give the checkpoint a unique and meaningful 
     * name using checkpoint(String)
     */
    static class ReactiveDemoWithCheckpoint {

        public static void main(String[] args) {
            reactive();    
        }

        private static void reactive() {
            int seconds = LocalTime.now().getSecond();

            Mono<Integer> source ;

            if( seconds % 2  == 0 )  {
                source = Flux.range(1, 11)
                             .elementAt(5)
                             .checkpoint("Source range(1,11)");
            } else if ( seconds % 3 ==0 ) {
                source = Flux.range(1, 4)
                             .elementAt(5)
                             .checkpoint("Source range(1,4)");
            } else {
                source = Flux.just(1,2,3)
                             .elementAt(5)
                             .checkpoint("Source just(1,2,3)");
            }

            source.block();
        }
    }

}
