package com.farhad.example.reactor.frequentnames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class BetterMultiThreadDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
     
        List<String> namesList = FrequentNamesDemo.generateNames();

        int batchSize = 1000;
        // int parallelism = (int) Math.ceil(namesList.size() / (double) batchSize);
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
                                    new ThreadFactory() {

                                        @Override
                                        public Thread newThread(Runnable r) {
                                            Thread t = new Thread(r);
                                            t.setDaemon(true);
                                            return t;
                                        }
                                    });
        Map<String,Long> finalCounts = new ConcurrentHashMap<>();
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < namesList.size() ; i+= batchSize) {
            int startIndxOfBatch = i ;
            int endIndxOfBatch = Math.min(startIndxOfBatch + batchSize, namesList.size());
            final List<String> batchedNamesList = namesList.subList(startIndxOfBatch, endIndxOfBatch);
            final BetterMultiThreadDemo.CountTask task = new CountTask(batchedNamesList,finalCounts, startIndxOfBatch, endIndxOfBatch);
            tasks.add(task);
        }
        executorService.invokeAny(tasks);
                   
        // Get max count
        Entry<String,Long> frequentEntry = finalCounts.entrySet()
                                         .stream()
                                         .max(Map.Entry.comparingByValue())
                                         .get();
        log.info("Frequent name: {} , Count: {}", frequentEntry.getKey(), frequentEntry.getValue());
    }

    @RequiredArgsConstructor
    static class CountTask implements Callable<Void> {
        private final List<String> namesList;
        private final Map<String,Long> finalCounts;
        private final int startIndex;
        private final int endIndec ;

        @Override
        public Void call() throws Exception {
            Map<String,Long> localCounts = new HashMap<>();
            log.info("Processing batch.... From {} To {}", startIndex, endIndec);
            for (String name : namesList) {
                localCounts.compute(name, (n, c) -> c == null ? 1L : c+1 );
            }
            for (Map.Entry<String,Long> entry : localCounts.entrySet()) {
                synchronized(finalCounts) {
                    if (finalCounts.containsKey(entry.getKey())) {
                        finalCounts.put(entry.getKey(), entry.getValue() + finalCounts.get(entry.getKey()));
                    } else {
                        finalCounts.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            return null;
        }
    }
}