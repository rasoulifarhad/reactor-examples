package com.farhad.example.reactivesystems.reactive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farhad.example.reactivesystems.domain.Order;
import com.farhad.example.reactivesystems.reactive.service.ShippingService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/shipments")
public class ShipmentController {

    @Autowired
    private ShippingService shippingService ;

    @PostMapping
    public Mono<Order> process(Order oirder) {
        return  shippingService.handleOrder(oirder);
    }
}
