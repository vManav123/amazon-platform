package com.stocker.orderservice.repository;

import com.stocker.orderservice.repository.entity.OrderItemEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderItemRepository extends R2dbcRepository<OrderItemEntity, String> {
    
    Flux<OrderItemEntity> findByOrderId(String orderId);
    
    Mono<Void> deleteByOrderId(String orderId);
}