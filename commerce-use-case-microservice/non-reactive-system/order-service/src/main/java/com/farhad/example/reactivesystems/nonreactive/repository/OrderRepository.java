package com.farhad.example.reactivesystems.nonreactive.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.farhad.example.reactivesystems.domain.Order;

import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends MongoRepository<Order,ObjectId> {
    
}
