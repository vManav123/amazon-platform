package com.stocker.dispatchservice.kafka;

import com.stocker.common.commands.dispatch.DispatchOrderCommand;
import com.stocker.common.events.order.OrderCreatedEvent;
import com.stocker.common.events.payment.PaymentProcessedEvent;
import com.stocker.dispatchservice.service.DispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Kafka consumer for dispatch-related events.
 * This class is responsible for consuming events from Kafka topics and processing them.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DispatchEventConsumer {

    private final DispatchService dispatchService;

    /**
     * Listen for OrderCreatedEvent from the order-events topic.
     * This is for informational purposes only, as dispatch typically waits for payment.
     *
     * @param event The OrderCreatedEvent received from Kafka
     */
    @KafkaListener(topics = "order-events", groupId = "dispatch-service-group")
    public void handleOrderCreatedEvent(@Payload OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent from Kafka: {}", event);
        // No action needed yet, as dispatch typically waits for payment confirmation
    }

    /**
     * Listen for PaymentProcessedEvent from the payment-events topic.
     * When a payment is successfully processed, we can initiate the dispatch process.
     *
     * @param event The PaymentProcessedEvent received from Kafka
     */
    @KafkaListener(topics = "payment-events", groupId = "dispatch-service-group")
    public void handlePaymentProcessedEvent(@Payload PaymentProcessedEvent event) {
        log.info("Received PaymentProcessedEvent from Kafka: {}", event);
        
        if ("COMPLETED".equals(event.getStatus())) {
            log.info("Payment completed for order {}, initiating dispatch process", event.getOrderId());
            
            // Create a dispatch order command
            DispatchOrderCommand command = DispatchOrderCommand.builder()
                .dispatchId(event.getPaymentId()) // Using paymentId as dispatchId for simplicity
                .orderId(event.getOrderId())
                .carrier("DEFAULT_CARRIER")
                .build();
            
            // Process the dispatch order
            dispatchService.processDispatch(command.getDispatchId(), command.getOrderId(), command.getCarrier())
                .subscribe(
                    dispatch -> log.info("Dispatch created: {}", dispatch),
                    error -> log.error("Error creating dispatch: {}", error.getMessage())
                );
        } else {
            log.info("Payment not completed for order {}, no dispatch needed", event.getOrderId());
        }
    }
}