package com.stocker.deliveryservice.repository;

import com.stocker.deliveryservice.repository.entity.DeliveryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DeliveryRepository extends ReactiveCrudRepository<DeliveryEntity, String> {
    
    Mono<DeliveryEntity> findByOrderId(String orderId);
    
    Mono<DeliveryEntity> findByTrackingNumber(String trackingNumber);
    
    Flux<DeliveryEntity> findByStatus(String status);
}