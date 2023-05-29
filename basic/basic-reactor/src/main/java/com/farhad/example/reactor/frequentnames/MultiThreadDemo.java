package com.farhad.example.reactor.frequentnames;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MultiThreadDemo {

    public static void main(String[] args) throws InterruptedException {

        List<String> namesList = FrequentNamesDemo.generateNames();

        // Agg counts
        int batchSize = 1000;
        List<MultiThreadDemo.CountTask> tasks = new ArrayList<>();
        Map<String,Long> finalCounts = new Hashtable<>();
        for (int i = 0; i < namesList.size() ; i+= batchSize) {
            int startIndxOfBatch = i ;
            int endIndxOfBatch = Math.min(startIndxOfBatch + batchSize, namesList.size());
            final List<String> batchedNamesList = namesList.subList(startIndxOfBatch, endIndxOfBatch);
            final MultiThreadDemo.CountTask task = new CountTask(batchedNamesList,finalCounts, startIndxOfBatch, endIndxOfBatch);
            tasks.add(task);
            task.setDaemon(true);
            task.start();
        }

        // wait until all task finished
        for (Thread thread : tasks) {
            thread.join();
        }

        // Get max count
        Entry<String,Long> frequentEntry = finalCounts.entrySet()
                                         .stream()
                                         .max(Map.Entry.comparingByValue())
                                         .get();
        log.info("Frequent name: {} , Count: {}", frequentEntry.getKey(), frequentEntry.getValue());
    }

    @RequiredArgsConstructor
    static class CountTask extends Thread {
        private final List<String> namesList;
        private final Map<String,Long> finalCounts;
        private final int startIndex;
        private final int endIndec ;

        @Override
        public void run() {
            Map<String,Long> localCounts = new Hashtable<>();
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

        }
    }
}