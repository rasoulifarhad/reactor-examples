package com.farhad.example.reactivesystems.reactive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.farhad.example.reactivesystems.constants.OrderStatus;
import com.farhad.example.reactivesystems.domain.Order;
import com.farhad.example.reactivesystems.reactive.repository.OrderRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class OrderService {

    String inventoryServiceUrl ="";
    String shippingServiceUrl ="" ;
    WebClient webClient = WebClient.create();
    
    @Autowired
    private OrderRepository  orderRepository;

     
    public Mono<Order> createOrder(Order order) {
        return Mono
                .just(order)
                .flatMap(orderRepository::save)
                .flatMap(o -> {
                    return webClient.method(HttpMethod.POST)
                                                        .uri(inventoryServiceUrl)
                                                        .body(BodyInserters.fromValue(o))
                                                        .retrieve()
                                                        .bodyToMono(Order.class)
                                                        ;
                })
                .onErrorResume(err -> {
                    return Mono.just(order.setOrderStatus(OrderStatus.FAILURE)
                    .setResponseMessage(err.getMessage()));
                })
                .flatMap(o -> {
                    if (!OrderStatus.FAILURE.equals(o.getOrderStatus())) {
                        return webClient.method(HttpMethod.POST)
                        .uri(shippingServiceUrl)
                        .body(BodyInserters.fromValue(o))
                        .retrieve()
                        .bodyToMono(Order.class)
                        ;
                    } else {
                        return Mono.just(o);
                    }
                })
                .onErrorResume(err -> {
                    return webClient.method(HttpMethod.POST)
                    .uri(inventoryServiceUrl)
                    .body(BodyInserters.fromValue(order))
                    .retrieve()
                    .bodyToMono(Order.class)
                    .map(o -> o.setOrderStatus(OrderStatus.FAILURE)
                        .setResponseMessage(err.getMessage()));
                })
                .map(o -> {
                    if (!OrderStatus.FAILURE.equals(o.getOrderStatus())) {
                        return order.setShippingDate(o.getShippingDate())
                        .setOrderStatus(OrderStatus.SUCCESS);
                    } else {
                        return order.setOrderStatus(OrderStatus.FAILURE)
                        .setResponseMessage(o.getResponseMessage());
                    }
                })
                .flatMap(orderRepository::save);        
    }

    public Flux<Order> getOrders() {
        return orderRepository.findAll();
    }

}
