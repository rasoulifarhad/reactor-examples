package com.farhad.example.reactivesystems.reactive.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farhad.example.reactivesystems.constants.OrderStatus;
import com.farhad.example.reactivesystems.domain.Order;
import com.farhad.example.reactivesystems.domain.Product;
import com.farhad.example.reactivesystems.reactive.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ProductService {
    
    @Autowired
    ProductRepository productRepository;

    public Mono<Order> handleOrder(Order order) {

        log.info("Handle order invoked with: {}", order);

        return Mono.just(order);

    }

    public Mono<Order> revertOrder(Order order) {

        log.info("Revert order invoked with: {}", order);

        return Mono.just(order);

    }

    public Flux<Product> getProducts() {


        return Flux.empty();

    }

}
