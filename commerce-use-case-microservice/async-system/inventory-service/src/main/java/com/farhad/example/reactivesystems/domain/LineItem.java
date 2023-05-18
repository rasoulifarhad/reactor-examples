package com.farhad.example.reactivesystems.domain;

import org.bson.types.ObjectId;

import com.farhad.example.reactivesystems.serdeser.ObjectIdSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineItem {

    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId productId;
    private int quantity;

    public LineItem setProductId(ObjectId productId) {
        this.productId = productId ;
        return this ;

    }

    public LineItem setQuantity(int quantity) {
        this.quantity = quantity ;
        return this ;
    }
}
