package com.farhad.example.reactor.frequentnames;

import java.time.LocalDateTime;
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
public class VerboseReactiveDemo2 {
    public static void main(String[] args) {
        List<String> namesList = FrequentNamesDemo.generateNames();

        int batchSize = namesList.size() / 10 ; ;
        Mono<Map<String,Long>> finalCounts=  Flux.fromIterable(namesList)
                                                    // Split to batches
                                                    .buffer(batchSize)
                                                    // Aggregate intermediate counts asynchronously
                                                    .flatMap(VerboseReactiveDemo2::processBatch)
                                                    .reduce(new HashMap<>(), VerboseReactiveDemo2::mergeIntermediateCount)
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
        log.info("[{}] Subscribing to batch processing ", LocalDateTime.now());
        return Flux.fromIterable(batch)
                    .groupBy(Function.identity())
                    .flatMap(group -> group.count().map(count -> Tuples.of(group.key(), count) ).subscribeOn(Schedulers.boundedElastic()) )
                    .collectMap(Tuple2::getT1, Tuple2::getT2)
                    .doOnSubscribe( __ -> log.info("[{}] Processing batch....", LocalDateTime.now()))
                    .subscribeOn(Schedulers.boundedElastic())
                    ;
    }
 
}
