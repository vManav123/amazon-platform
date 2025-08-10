package com.stocker.deliveryservice.kafka;

import com.stocker.common.commands.delivery.DeliverOrderCommand;
import com.stocker.common.events.dispatch.OrderDispatchedEvent;
import com.stocker.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Kafka consumer for delivery-related events.
 * This class is responsible for consuming events from Kafka topics and processing them.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventConsumer {

    private final DeliveryService deliveryService;

    /**
     * Listen for OrderDispatchedEvent from the dispatch-events topic.
     * When an order is dispatched, we can initiate the delivery process.
     *
     * @param event The OrderDispatchedEvent received from Kafka
     */
    @KafkaListener(topics = "dispatch-events", groupId = "delivery-service-group")
    public void handleOrderDispatchedEvent(@Payload OrderDispatchedEvent event) {
        log.info("Received OrderDispatchedEvent from Kafka: {}", event);
        
        if ("DISPATCHED".equals(event.getStatus())) {
            log.info("Order dispatched with tracking number {}, initiating delivery process", event.getTrackingNumber());
            
            // Create a deliver order command
            DeliverOrderCommand command = DeliverOrderCommand.builder()
                .deliveryId(event.getDispatchId() + "-delivery") // Generate a delivery ID based on dispatch ID
                .dispatchId(event.getDispatchId())
                .orderId(event.getOrderId())
                .trackingNumber(event.getTrackingNumber())
                .deliveryAddress("Customer Address") // This should come from the order
                .estimatedDeliveryTime(LocalDateTime.now().plusDays(3))
                .build();
            
            // Process the delivery
            deliveryService.processDelivery(
                    command.getDeliveryId(), 
                    command.getDispatchId(), 
                    command.getOrderId(), 
                    command.getTrackingNumber(), 
                    command.getDeliveryAddress(), 
                    command.getEstimatedDeliveryTime()
                )
                .subscribe(
                    delivery -> log.info("Delivery created: {}", delivery),
                    error -> log.error("Error creating delivery: {}", error.getMessage())
                );
        } else {
            log.info("Order not dispatched properly, status: {}, no delivery needed", event.getStatus());
        }
    }
}