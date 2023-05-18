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
import com.farhad.example.reactivesystems.domain.Shipment;
import com.farhad.example.reactivesystems.nonreactive.repository.ShipmentRepository;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.LocalTime;


@Slf4j
@Service
public class ShipmentService {
    
    @Autowired
    ShipmentRepository shipmentRepository;

    @Transactional
    public Order handleOrder(Order order) {

        log.info("Handle order invoked with: {}", order);
        LocalDate shippingDate = null ;

        if (LocalTime.now().isAfter(LocalTime.parse("10:00")) 
                        && LocalTime.now().isBefore(LocalTime.parse("18:00"))) {

            shippingDate = LocalDate.now().plusDays(1);
        } else {
            throw new RuntimeException("The current time is off the limits to place order.");
        }

        shipmentRepository.save(new Shipment()
                                        .setAddress(order.getShippingAddress())
                                        .setShippingDate(shippingDate)) ;

        return order.setShippingDate(shippingDate)
                    .setOrderStatus(OrderStatus.SUCCESS);

 

    }

 
}
