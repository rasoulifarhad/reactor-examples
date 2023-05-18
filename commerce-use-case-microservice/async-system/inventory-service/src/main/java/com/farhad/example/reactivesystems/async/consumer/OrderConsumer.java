package com.farhad.example.reactivesystems.async.consumer;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.farhad.example.reactivesystems.async.producer.OrderProducer;
import com.farhad.example.reactivesystems.constants.OrderStatus;
import com.farhad.example.reactivesystems.domain.Order;
import com.farhad.example.reactivesystems.reactive.service.ProductService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderConsumer {

    @Autowired
    private ProductService productService ;

    @Autowired
    private OrderProducer orderProducer ;
    
    @KafkaListener(topics = "orders" , groupId = "invetory")
    public void consume(Order order ) throws IOException {

        if(OrderStatus.RESERVE_INVENTORY.equals(order.getOrderStatus())) {
            productService
                .handleOrder(order)
                .doOnSuccess(o -> {
                    orderProducer.sendMessage(order.setOrderStatus(OrderStatus.INVENTORY_SUCCESS));
                })
                .doOnError(e -> {
                    orderProducer.sendMessage(
                        order.setOrderStatus(OrderStatus.INVENTORY_FAILURE)
                             .setResponseMessage(e.getMessage()));
                }).subscribe();
        } else  if (OrderStatus.REVERT_INVENTORY.equals(order.getOrderStatus())){
            productService
                .revertOrder(order) 
                .doOnSuccess(o -> {
                    orderProducer.sendMessage(order.setOrderStatus(OrderStatus.INVENTORY_REVERT_SUCCESS));
                })
                .doOnError(e -> {
                    orderProducer.sendMessage(
                                order.setOrderStatus(OrderStatus.INVENTORY_REVERT_FAILURE)
                                     .setResponseMessage(e.getMessage()));
                }).subscribe();
        }

    }
}
