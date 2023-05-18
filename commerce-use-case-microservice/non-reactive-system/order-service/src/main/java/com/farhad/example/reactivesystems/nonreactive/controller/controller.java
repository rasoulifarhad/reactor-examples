package com.farhad.example.reactivesystems.nonreactive.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farhad.example.reactivesystems.constants.OrderStatus;
import com.farhad.example.reactivesystems.domain.Order;
import java.util.List;
import com.farhad.example.reactivesystems.nonreactive.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/orders")
public class controller {

    @Autowired
    private OrderService orderService ;

    @GetMapping
    public List<Order> getAll() {

        return orderService.getOrders();
    }

    @PostMapping
    public Order createOrder(@ RequestBody Order order) {

        Order processedOrder = orderService.createOrder(order);
        if (OrderStatus.FAILURE.equals(processedOrder.getOrderStatus())){
            throw new RuntimeException("Order processing failed, please try again later.");

        }
        return processedOrder ;
    }
   
    
}
