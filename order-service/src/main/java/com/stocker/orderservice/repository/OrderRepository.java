package com.stocker.orderservice.repository;

import com.stocker.orderservice.repository.entity.OrderEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends R2dbcRepository<OrderEntity, String> {
    
    Flux<OrderEntity> findByUserId(String userId);
    
    Mono<OrderEntity> findByCartId(String cartId);
}