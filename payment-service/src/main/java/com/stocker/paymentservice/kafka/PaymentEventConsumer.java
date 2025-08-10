package com.stocker.paymentservice.kafka;

import com.stocker.common.commands.payment.ProcessPaymentCommand;
import com.stocker.common.events.order.OrderCreatedEvent;
import com.stocker.paymentservice.domain.PaymentStatus;
import com.stocker.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Kafka consumer for payment-related events.
 * This class is responsible for consuming events from Kafka topics and processing them.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final PaymentService paymentService;

    /**
     * Listen for OrderCreatedEvent from the order-events topic.
     * When an order is created, we need to process the payment.
     *
     * @param event The OrderCreatedEvent received from Kafka
     */
    @KafkaListener(topics = "order-events", groupId = "payment-service-group")
    public void handleOrderCreatedEvent(@Payload OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent from Kafka: {}", event);
        
        // Create a process payment command
        ProcessPaymentCommand command = ProcessPaymentCommand.builder()
            .paymentId(event.getOrderId() + "-payment") // Generate a payment ID based on order ID
            .orderId(event.getOrderId())
            .amount(event.getTotalAmount())
            .paymentMethod("CREDIT_CARD") // Default payment method
            .build();
        
        // Process the payment
        paymentService.processPayment(
                command.getPaymentId(), 
                command.getOrderId(), 
                command.getAmount(), 
                command.getPaymentMethod()
            )
            .subscribe(
                payment -> log.info("Payment processed: {}", payment),
                error -> log.error("Error processing payment: {}", error.getMessage())
            );
    }
}