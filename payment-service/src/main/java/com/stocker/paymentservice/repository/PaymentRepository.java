package com.stocker.paymentservice.repository;

import com.stocker.paymentservice.repository.entity.PaymentEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PaymentRepository extends R2dbcRepository<PaymentEntity, String> {
    
    Flux<PaymentEntity> findByOrderId(String orderId);
    
    Mono<PaymentEntity> findByTransactionId(String transactionId);
}