package com.stocker.dispatchservice.repository;

import com.stocker.dispatchservice.repository.entity.DispatchEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DispatchRepository extends R2dbcRepository<DispatchEntity, String> {
    
    Mono<DispatchEntity> findByOrderId(String orderId);
    
    Mono<DispatchEntity> findByTrackingNumber(String trackingNumber);
    
    Flux<DispatchEntity> findByCarrier(String carrier);
}