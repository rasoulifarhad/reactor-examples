package com.farhad.example.reactivesystems.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.farhad.example.reactivesystems.constants.OrderStatus;
import com.farhad.example.reactivesystems.serdeser.ObjectIdSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;


@Data
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    @Id
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;
    private String userId;
    private List<LineItem> lineItems;
    private Long total;
    private String paymentMode;
    private Address shippingAddress;
    private Date shippingDate;
    private OrderStatus orderStatus;
    private String responseMessage;

    public Order setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
        return this;
    }

    public Order setShippingDate(Date shippingDate) {
        this.shippingDate = shippingDate;
        return this;
    }

    public Order setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public Order setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
        return this;
    }

    
}
