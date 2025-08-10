package com.stocker.paymentservice.service;

import com.stocker.common.commands.payment.ProcessPaymentCommand;
import com.stocker.common.events.payment.PaymentProcessedEvent;
import com.stocker.paymentservice.domain.PaymentStatus;
import com.stocker.paymentservice.kafka.PaymentEventProducer;
import com.stocker.paymentservice.repository.PaymentRepository;
import com.stocker.paymentservice.repository.entity.PaymentEntity;
import com.stocker.paymentservice.service.dto.PaymentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CommandGateway commandGateway;
    private final PaymentEventProducer paymentEventProducer;

    @Override
    public Mono<PaymentDTO> processPayment(String paymentId, String orderId, BigDecimal amount, String paymentMethod) {
        ProcessPaymentCommand command = ProcessPaymentCommand.builder()
                .paymentId(paymentId)
                .orderId(orderId)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .build();
        
        return Mono.fromFuture(commandGateway.send(command))
                .then(Mono.defer(() -> 
                    // Wait for the payment entity to be created by the event handler
                    // Use a retry mechanism with exponential backoff
                    paymentRepository.findById(paymentId)
                        .retryWhen(reactor.util.retry.Retry.backoff(3, java.time.Duration.ofMillis(300))
                            .doBeforeRetry(retrySignal -> 
                                log.debug("Retrying to find payment entity: {}, attempt: {}", 
                                    paymentId, retrySignal.totalRetries())
                            )
                        )
                        .switchIfEmpty(Mono.error(new RuntimeException("Payment not found after processing: " + paymentId)))
                ))
                .map(this::mapToPaymentDTO)
                .flatMap(payment -> {
                    // Create and send a PaymentProcessedEvent
                    PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                            .paymentId(payment.getId())
                            .orderId(payment.getOrderId())
                            .amount(payment.getAmount())
                            .status(payment.getStatus().toString())
                            .timestamp(Instant.now())
                            .build();
                    
                    return paymentEventProducer.sendPaymentProcessedEvent(event)
                            .thenReturn(payment);
                })
                .doOnSuccess(payment -> log.info("Payment processed successfully: {}, status: {}", 
                    payment.getId(), payment.getStatus()))
                .doOnError(error -> log.error("Error processing payment for order: {}", orderId, error));
    }

    @Override
    public Mono<PaymentDTO> getPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .map(this::mapToPaymentDTO);
    }

    @Override
    public Flux<PaymentDTO> getPaymentsByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(this::mapToPaymentDTO);
    }

    @Override
    public Mono<PaymentDTO> refundPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .flatMap(paymentEntity -> {
                    if (!PaymentStatus.COMPLETED.toString().equals(paymentEntity.getStatus())) {
                        return Mono.error(new IllegalStateException("Payment cannot be refunded because it is not completed"));
                    }
                    
                    paymentEntity.setStatus(PaymentStatus.REFUNDED.toString());
                    paymentEntity.setUpdatedAt(Instant.now());
                    
                    return paymentRepository.save(paymentEntity);
                })
                .map(this::mapToPaymentDTO);
    }
    
    private PaymentDTO mapToPaymentDTO(PaymentEntity paymentEntity) {
        return PaymentDTO.builder()
                .id(paymentEntity.getId())
                .orderId(paymentEntity.getOrderId())
                .amount(paymentEntity.getAmount())
                .status(PaymentStatus.valueOf(paymentEntity.getStatus()))
                .paymentMethod(paymentEntity.getPaymentMethod())
                .transactionId(paymentEntity.getTransactionId())
                .createdAt(paymentEntity.getCreatedAt())
                .updatedAt(paymentEntity.getUpdatedAt())
                .build();
    }
}