package com.farhad.example.reactivesystems.nonreactive.service;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farhad.example.reactivesystems.constants.OrderStatus;
import com.farhad.example.reactivesystems.domain.Order;
import com.farhad.example.reactivesystems.nonreactive.repository.OrderRepository;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
public class OrderService {
    
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate ;

    String inventoryServiceUrl = "" ;
    String shippingServiceUrl = "" ;

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    // @Transactional
    public Order createOrder(Order order) {

        boolean success = true ;

        Order savedOrder = orderRepository.save(order);

        Order inventoryResponse = null;

        try {
            restTemplate.postForObject(inventoryServiceUrl, order,Order.class);

        } catch (Exception ex) {
            success = false ;
        }

        Order shippingResponse = null;

        try {
            restTemplate.postForObject(shippingServiceUrl, order,Order.class);

        } catch (Exception ex) {
            success = false ;
            HttpEntity<Order> deleteRequest = new HttpEntity<>(order);
            ResponseEntity<Order> deleteResponse = restTemplate.exchange(inventoryServiceUrl, 
                                                                        HttpMethod.DELETE,
                                                                        deleteRequest,
                                                                        Order.class);
        }

        if (success) {
            savedOrder.setOrderStatus(OrderStatus.SUCCESS);
            savedOrder.setShippingDate(shippingResponse.getShippingDate());
        } else {
            savedOrder.setOrderStatus(OrderStatus.FAILURE);
        }
        return orderRepository.save(savedOrder) ;
    }

 
}
