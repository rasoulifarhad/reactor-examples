package com.farhad.example.reactor.frequentnames;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * See https://www.javacodegeeks.com/2022/09/reactives-looming-doom-part-i-evolution.html
 */
public class FrequentNamesDemo {

    private static final int NAMES_COUNT = 100_000_000;
    public static void main(String[] args) {
        
    }
    static List<String> generateNames()  {

            //Generate names
            Random r = new Random();
            List<String> names = Arrays.asList("Joe", "Monica", "Chandler", "Phoebe", "Rachel", "Ross", "Janice");
            List<String> namesList = IntStream.range(0, NAMES_COUNT)
                                              .mapToObj(value -> names.get(r.nextInt(names.size())))
                                              .collect(Collectors.toList());
            return namesList;
    }
}
