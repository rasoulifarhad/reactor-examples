package com.farhad.example.reactor.frequentnames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SingleThreadedDemo {
    public static void main(String[] args) {

        List<String> namesList = FrequentNamesDemo.generateNames();

        // Agg counts
        Map<String,Long> counts = new HashMap<>();
        for (String name : namesList) {
            counts.compute(name, (n, c) -> c == null ? 1L : c+1 );
        }

        // Get max count
        Entry<String,Long> frequentEntry = counts.entrySet()
                                    .stream()
                                    .max(Map.Entry.comparingByValue())
                                    .get();
        log.info("Frequent name: {}, Count: {}", frequentEntry.getKey(), frequentEntry.getValue());
    }
}
