package com.farhad.example.reactivesystems.serdeser;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@Component
public class ObjectIdSerializer extends JsonSerializer<ObjectId>{

    @Override
    public void serialize(ObjectId value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
    
}
