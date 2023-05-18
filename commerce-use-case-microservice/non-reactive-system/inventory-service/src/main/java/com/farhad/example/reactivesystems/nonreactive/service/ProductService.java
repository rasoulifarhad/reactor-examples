package com.farhad.example.reactivesystems.nonreactive.service;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farhad.example.reactivesystems.constants.OrderStatus;
import com.farhad.example.reactivesystems.domain.Order;
import com.farhad.example.reactivesystems.domain.Product;
import com.farhad.example.reactivesystems.nonreactive.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class ProductService {
    
    @Autowired
    ProductRepository productRepository;

    @Transactional
    public Order handleOrder(Order order) {

        log.info("Handle order invoked with: {}", order);

        order
            .getLineItems()
            .forEach(l -> {
                Product p =  productRepository
                                .findById(l.getProductId())
                                .orElseThrow(() -> new RuntimeException("Could not find the product: " + l.getProductId()));
                if (p.getStock() >= l.getQuantity() ) {
                    p.setStock(p.getStock() - l.getQuantity());
                    productRepository.save(p);
                } else {
                    throw new RuntimeException("Product is out of stock: " + l.getProductId());
                }
            });


        return order.setOrderStatus(OrderStatus.SUCCESS);

    }

    @Transactional
    public Order revertOrder(Order order) {

        log.info("Revert order invoked with: {}", order);

        order
            .getLineItems()
            .forEach(l -> {
                Product p =  productRepository
                                .findById(l.getProductId())
                                .orElseThrow(() -> new RuntimeException("Could not find the product: " + l.getProductId()));

                p.setStock(p.getStock() + l.getQuantity());
                productRepository.save(p);
            });


        return order.setOrderStatus(OrderStatus.SUCCESS);

    }

    public List<Product> getProducts() {

        return productRepository.findAll();

    }

}
