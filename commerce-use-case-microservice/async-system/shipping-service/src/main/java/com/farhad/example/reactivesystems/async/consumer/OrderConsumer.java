package com.farhad.example.reactivesystems.async.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.farhad.example.reactivesystems.async.producer.OrderProducer;
import com.farhad.example.reactivesystems.constants.OrderStatus;
import com.farhad.example.reactivesystems.domain.Order;
import com.farhad.example.reactivesystems.reactive.service.ShippingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderConsumer {
    
    @Autowired
    private ShippingService shippingService;

    @Autowired
    private OrderProducer orderProducer ;

    @KafkaListener(topics = "orders" ,groupId = "shipping")
    public void consume(Order order) {
        log.info("Order received to process: {}", order);

        if (OrderStatus.PREPARE_SHIPPING.equals(order.getOrderStatus())) {
            shippingService.handleOrder(order)
              .doOnSuccess(o -> {
                  orderProducer.sendMessage(order.setOrderStatus(OrderStatus.SHIPPING_SUCCESS)
                    .setShippingDate(o.getShippingDate()));
              })
              .doOnError(e -> {
                  orderProducer.sendMessage(order.setOrderStatus(OrderStatus.SHIPPING_FAILURE)
                    .setResponseMessage(e.getMessage()));
              }).subscribe();
        }        

    }
}
