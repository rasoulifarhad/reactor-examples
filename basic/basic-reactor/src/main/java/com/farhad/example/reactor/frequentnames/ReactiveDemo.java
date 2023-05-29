package com.farhad.example.reactor.frequentnames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.math.MathFlux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Slf4j
public class ReactiveDemo {
    
    private static final int BATCH_SIZE = 1000;
    public static void main(String[] args) {
        List<String> namesList = FrequentNamesDemo.generateNames();

        int batchSize = BATCH_SIZE ;
        Mono<Map<String,Long>> finalCounts=  Flux.fromIterable(namesList)
                                                    .buffer(batchSize)
                                                    .flatMap(ReactiveDemo::processBatch)
                                                    .reduce(new HashMap<>(), ReactiveDemo::mergeIntermediateCount)
                                                    ;
        Flux<Entry<String,Long>> finalCountsEntry = finalCounts.flatMapIterable(t -> t.entrySet());
        // Mono<Entry<String,Long>> frequentEntry = MathFlux.max(finalCountsEntry, Map.Entry.comparingByValue(null));
        MathFlux.max(finalCountsEntry, Map.Entry.comparingByValue())
                .subscribe(entry -> log.info("Frequent name: {} , Count: {}", entry.getKey(), entry.getValue()));

    }
    private static Map<String,Long> mergeIntermediateCount(Map<String,Long> acc, Map<String,Long> intemediatResult) {
        intemediatResult.forEach((name, count) ->  acc.merge(name, count, Long::sum));
        return acc;
    }
    private static Mono<Map<String,Long>> processBatch(List<String> batch) {
        return Flux.fromIterable(batch)
                    .groupBy(Function.identity())
                    .flatMap(group -> group.count().map(count -> Tuples.of(group.key(), count) ) )
                    .collectMap(Tuple2::getT1, Tuple2::getT2)
                    .doOnSubscribe(s -> log.info("Processing batch...."))
                    .subscribeOn(Schedulers.boundedElastic())
                    ;
    }
}
