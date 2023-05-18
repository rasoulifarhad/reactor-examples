package com.farhad.example.reactivesystems.reactive.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.farhad.example.reactivesystems.domain.Order;

import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends ReactiveMongoRepository<Order,ObjectId>{
    
}
