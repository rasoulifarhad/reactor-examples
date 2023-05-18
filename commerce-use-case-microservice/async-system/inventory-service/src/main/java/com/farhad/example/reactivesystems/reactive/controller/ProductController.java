package com.farhad.example.reactivesystems.reactive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farhad.example.reactivesystems.domain.Product;
import com.farhad.example.reactivesystems.domain.Order;

import com.farhad.example.reactivesystems.reactive.service.ProductService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    ProductService productService ;

    @GetMapping
    public Flux<Product> getallProducts() {
        log.info("Get all products invoked.");
        return productService.getProducts();

    }

    @PostMapping
    public Mono<Order> processOrder(@RequestBody Order order) {
        return productService.handleOrder(order);
    }

    @DeleteMapping
    public Mono<Order> revertOrder(@RequestBody Order order ) {
        return productService.revertOrder(order);
    }
}
