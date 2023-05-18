package com.farhad.example.reactivesystems.reactive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farhad.example.reactivesystems.domain.Order;
import com.farhad.example.reactivesystems.reactive.repository.ShipmentRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ShippingService {
    
    @Autowired
    private ShipmentRepository  shipmentRepository;

    public Mono<Order> handleOrder(Order order ) {

        return Mono.just(order);
    }
}
