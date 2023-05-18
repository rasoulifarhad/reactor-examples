package com.farhad.example.reactivesystems.reactive.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.farhad.example.reactivesystems.domain.Product;

import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product,ObjectId> {
    
}
