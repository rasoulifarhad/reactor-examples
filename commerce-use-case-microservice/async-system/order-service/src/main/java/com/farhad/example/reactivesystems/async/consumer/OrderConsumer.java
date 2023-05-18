package com.farhad.example.reactivesystems.async.consumer;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.farhad.example.reactivesystems.async.producer.OrderProducer;
import com.farhad.example.reactivesystems.constants.OrderStatus;
import com.farhad.example.reactivesystems.domain.Order;
import com.farhad.example.reactivesystems.reactive.repository.OrderRepository;
import com.farhad.example.reactivesystems.reactive.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderConsumer {
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProducer orderProducer ;

    @KafkaListener(topics = "orders", groupId = "inventory")
    public void consume(Order order) throws IOException {

        if (OrderStatus.INITIATION_SUCCESS.equals(order.getOrderStatus())) {
            orderRepository.findById(order.getId())
              .map(o -> {
                  orderProducer.sendMessage(o.setOrderStatus(OrderStatus.RESERVE_INVENTORY));
                  return o.setOrderStatus(order.getOrderStatus())
                    .setResponseMessage(order.getResponseMessage());
              })
              .flatMap(orderRepository::save)
              .subscribe();
        } else if ("INVENTORY-SUCCESS".equals(order.getOrderStatus())) {
            orderRepository.findById(order.getId())
              .map(o -> {
                  orderProducer.sendMessage(o.setOrderStatus(OrderStatus.PREPARE_SHIPPING));
                  return o.setOrderStatus(order.getOrderStatus())
                    .setResponseMessage(order.getResponseMessage());
              })
              .flatMap(orderRepository::save)
              .subscribe();
        } else if ("SHIPPING-FAILURE".equals(order.getOrderStatus())) {
            orderRepository.findById(order.getId())
              .map(o -> {
                  orderProducer.sendMessage(o.setOrderStatus(OrderStatus.REVERT_INVENTORY));
                  return o.setOrderStatus(order.getOrderStatus())
                    .setResponseMessage(order.getResponseMessage());
              })
              .flatMap(orderRepository::save)
              .subscribe();
        } else {
            orderRepository.findById(order.getId())
              .map(o -> {
                  return o.setOrderStatus(order.getOrderStatus())
                    .setResponseMessage(order.getResponseMessage());
              })
              .flatMap(orderRepository::save)
              .subscribe();
        }
    }
}
