package com.stocker.paymentservice.service;

import com.stocker.paymentservice.service.dto.PaymentDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface PaymentService {
    
    Mono<PaymentDTO> processPayment(String paymentId, String orderId, BigDecimal amount, String paymentMethod);
    
    Mono<PaymentDTO> getPayment(String paymentId);
    
    Flux<PaymentDTO> getPaymentsByOrderId(String orderId);
    
    Mono<PaymentDTO> refundPayment(String paymentId);
}