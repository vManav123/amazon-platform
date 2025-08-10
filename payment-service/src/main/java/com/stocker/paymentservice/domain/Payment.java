package com.stocker.paymentservice.domain;

import com.stocker.common.commands.payment.ProcessPaymentCommand;
import com.stocker.common.events.payment.PaymentProcessedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Aggregate
@NoArgsConstructor
@Slf4j
public class Payment {

    @AggregateIdentifier
    private String paymentId;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String paymentMethod;
    private Instant createdAt;

    @CommandHandler
    public Payment(ProcessPaymentCommand command) {
        log.info("Processing payment for order: {}, amount: {}", command.getOrderId(), command.getAmount());
        
        try {
            // In a real implementation, we would integrate with a payment gateway
            // For simplicity, we'll simulate payment processing
            PaymentProcessingResult result = processPayment(
                    command.getOrderId(), 
                    command.getAmount(), 
                    command.getPaymentMethod()
            );
            
            log.info("Payment processed for order: {}, status: {}, transactionId: {}", 
                    command.getOrderId(), result.getStatus(), result.getTransactionId());
            
            AggregateLifecycle.apply(PaymentProcessedEvent.builder()
                    .paymentId(command.getPaymentId())
                    .orderId(command.getOrderId())
                    .amount(command.getAmount())
                    .status(com.stocker.common.models.Payment.PaymentStatus.valueOf(result.getStatus().toString()))
                    .paymentMethod(com.stocker.common.models.Payment.PaymentMethod.valueOf(command.getPaymentMethod()))
                    .transactionId(result.getTransactionId())
                    .processedAt(java.time.LocalDateTime.now())
                    .build());
        } catch (Exception ex) {
            log.error("Error processing payment for order: {}", command.getOrderId(), ex);
            
            // Even in case of technical errors, we need to apply an event to maintain the saga
            AggregateLifecycle.apply(PaymentProcessedEvent.builder()
                    .paymentId(command.getPaymentId())
                    .orderId(command.getOrderId())
                    .amount(command.getAmount())
                    .status(com.stocker.common.models.Payment.PaymentStatus.FAILED)
                    .paymentMethod(com.stocker.common.models.Payment.PaymentMethod.valueOf(command.getPaymentMethod()))
                    .transactionId(null)
                    .processedAt(java.time.LocalDateTime.now())
                    .build());
        }
    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
        this.amount = event.getAmount();
        this.status = PaymentStatus.valueOf(event.getStatus().toString());
        this.paymentMethod = event.getPaymentMethod().toString();
        this.createdAt = event.getProcessedAt().atZone(java.time.ZoneId.systemDefault()).toInstant();
    }
    
    private PaymentProcessingResult processPayment(String orderId, BigDecimal amount, String paymentMethod) {
        log.info("Processing payment for order: {}, amount: {}, method: {}", orderId, amount, paymentMethod);
        
        // Simulate different payment scenarios based on amount
        // In a real implementation, this would call an actual payment gateway
        String transactionId = UUID.randomUUID().toString();
        PaymentStatus status;
        
        try {
            // Simulate different payment scenarios
            if (amount.compareTo(BigDecimal.valueOf(1000)) > 0) {
                // Amounts over 1000 have a higher chance of failure
                status = Math.random() < 0.7 ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;
            } else if ("BITCOIN".equalsIgnoreCase(paymentMethod)) {
                // Simulate cryptocurrency processing
                Thread.sleep(500); // Simulate blockchain confirmation delay
                status = Math.random() < 0.8 ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;
            } else if ("CREDIT_CARD".equalsIgnoreCase(paymentMethod)) {
                // Credit card processing
                status = Math.random() < 0.95 ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;
            } else {
                // Default payment method
                status = Math.random() < 0.9 ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;
            }
            
            // Simulate occasional processing errors
            if (Math.random() < 0.05) {
                throw new RuntimeException("Simulated payment gateway error");
            }
            
            return new PaymentProcessingResult(status, transactionId);
        } catch (InterruptedException e) {
            log.error("Payment processing interrupted", e);
            Thread.currentThread().interrupt();
            return new PaymentProcessingResult(PaymentStatus.FAILED, null);
        }
    }
    
    @lombok.Value
    private static class PaymentProcessingResult {
        PaymentStatus status;
        String transactionId;
    }
}