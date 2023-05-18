package com.farhad.example.reactivesystems.nonreactive;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farhad.example.reactivesystems.domain.Order;

import com.farhad.example.reactivesystems.nonreactive.service.ShipmentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/shipments")
public class controller {

    @Autowired
    private ShipmentService shipmentService ;

    @PostMapping
    public Order process(@RequestBody Order order) {
        return shipmentService.handleOrder(order);
    }

    
}
