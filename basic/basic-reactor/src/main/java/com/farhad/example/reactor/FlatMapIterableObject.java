package com.farhad.example.reactor;


import java.util.List;
import lombok.Value;


@Value
public class FlatMapIterableObject {
    
    IterableObject iterableObject;

    @Value
    public static class IterableObject
    {
        List<ValueObject> valueObjects;
    }

    @Value
    public static class ValueObject
    {
        String value;
    }
}
