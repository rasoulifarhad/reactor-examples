package com.farhad.example.reactor.frequentnames;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FrequentNamesDemo {

    public static void main(String[] args) {
        
    }
    static List<String> generateNames()  {

            //Generate names
            Random r = new Random();
            List<String> names = Arrays.asList("Joe", "Monica", "Chandler", "Phoebe", "Rachel", "Ross", "Janice");
            List<String> namesList = IntStream.range(0, 1_000_000)
                                              .mapToObj(value -> names.get(r.nextInt(names.size())))
                                              .collect(Collectors.toList());
            return namesList;
    }
}
