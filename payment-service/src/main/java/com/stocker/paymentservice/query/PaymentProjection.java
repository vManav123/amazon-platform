package com.stocker.paymentservice.query;

import com.stocker.common.events.payment.PaymentProcessedEvent;
import com.stocker.paymentservice.domain.PaymentStatus;
import com.stocker.paymentservice.repository.PaymentRepository;
import com.stocker.paymentservice.repository.entity.PaymentEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProjection {

    private final PaymentRepository paymentRepository;

    @EventHandler
    public void on(PaymentProcessedEvent event) {
        log.info("Handling PaymentProcessedEvent: {}, status: {}", event.getPaymentId(), event.getStatus());
        
        // Convert LocalDateTime to Instant
        java.time.Instant processedAt = event.getProcessedAt().atZone(java.time.ZoneId.systemDefault()).toInstant();
        
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .id(event.getPaymentId())
                .orderId(event.getOrderId())
                .amount(event.getAmount())
                .status(event.getStatus().toString())
                .paymentMethod(event.getPaymentMethod().toString())
                .transactionId(event.getTransactionId() != null ? event.getTransactionId() : UUID.randomUUID().toString())
                .createdAt(processedAt)
                .updatedAt(processedAt)
                .build();
        
        paymentRepository.save(paymentEntity)
            .doOnSuccess(savedPayment -> log.info("Payment saved: {}, status: {}", 
                savedPayment.getId(), savedPayment.getStatus()))
            .doOnError(error -> log.error("Error saving payment: {}", error.getMessage()))
            .subscribe();
    }

    @QueryHandler
    public Mono<PaymentEntity> findPayment(FindPaymentQuery query) {
        log.info("Handling FindPaymentQuery: {}", query.getPaymentId());
        return paymentRepository.findById(query.getPaymentId());
    }

    @QueryHandler
    public Flux<PaymentEntity> findPaymentsByOrderId(FindPaymentsByOrderIdQuery query) {
        log.info("Handling FindPaymentsByOrderIdQuery: {}", query.getOrderId());
        return paymentRepository.findByOrderId(query.getOrderId());
    }

    @QueryHandler
    public Mono<PaymentEntity> findPaymentByTransactionId(FindPaymentByTransactionIdQuery query) {
        log.info("Handling FindPaymentByTransactionIdQuery: {}", query.getTransactionId());
        return paymentRepository.findByTransactionId(query.getTransactionId());
    }
}