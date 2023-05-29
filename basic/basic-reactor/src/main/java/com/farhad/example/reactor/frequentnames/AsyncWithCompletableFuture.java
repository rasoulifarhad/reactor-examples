package com.farhad.example.reactor.frequentnames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class AsyncWithCompletableFuture {

    private static final int BATCH_SIZE= 100000;
    public static void main(String[] args) throws InterruptedException {
        
        List<String> namesList = FrequentNamesDemo.generateNames();

        int batchSize =BATCH_SIZE ;
        CompletableFuture<Map<String, Long>> finalCompletableFuture =  IntStream.iterate(0, batchStart -> batchStart + batchSize )
                                                                            .limit((int) Math.ceil(namesList.size() / (double)batchSize))
                                                                            .mapToObj(batchStart -> prepareBatch(namesList,batchStart , batchSize))
                                                                            .reduce((left, right) -> combineMaps(left, right))
                                                                            .get();
        Map<String,Long> finalCounts = finalCompletableFuture.join();

        Entry<String,Long> frequentEntry = finalCounts.entrySet()
                                         .stream()
                                         .max(Map.Entry.comparingByValue())
                                         .get();
        log.info("Frequent name: {} , Count: {}", frequentEntry.getKey(), frequentEntry.getValue());
        Thread.sleep(5000);

    }



    private static  CompletableFuture<Map<String, Long>> combineMaps(CompletableFuture<Map<String, Long>> left,
            CompletableFuture<Map<String, Long>> right) {
        return left.thenCombineAsync(right, (l, r) -> mergeCounts(l, r));
    }


    private static Map<String,Long> mergeCounts(Map<String, Long> l, Map<String, Long> r) {
        Map<String, Long> accumulator = new HashMap<>(l);
        r.forEach((k, v) -> accumulator.compute(k, (n, c) -> n == null ? v : c + v) );
        return accumulator;
    }



    private static CompletableFuture<Map<String,Long>> prepareBatch(List<String> namesList, int batchStart, int batchSize) {

        return CompletableFuture.supplyAsync(() -> {
            Map<String,Long> localCounts = new ConcurrentHashMap<>();
            int batchEndIndex = Math.min(batchStart + batchSize , namesList.size());
            log.info("Processing batch.... From {} To {}", batchStart, batchEndIndex);
            for (String name : namesList.subList(batchStart, batchEndIndex)) {
                localCounts.compute(name, (n, c) ->  c == null ? 1L : c + 1);
            }
            return localCounts;
        });
    }

}