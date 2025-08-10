package com.stocker.orderservice.kafka;

import com.stocker.common.commands.order.UpdateOrderStatusCommand;
import com.stocker.common.events.delivery.OrderDeliveredEvent;
import com.stocker.orderservice.domain.OrderStatus;
import com.stocker.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for order-related events.
 * This class is responsible for consuming events from Kafka topics and processing them.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final OrderService orderService;

    /**
     * Listen for OrderDeliveredEvent from the delivery-events topic.
     * When an order is delivered, update the order status to DELIVERED.
     *
     * @param event The OrderDeliveredEvent received from Kafka
     */
    @KafkaListener(topics = "delivery-events", groupId = "order-service-group")
    public void handleOrderDeliveredEvent(@Payload OrderDeliveredEvent event) {
        log.info("Received OrderDeliveredEvent from Kafka: {}", event);
        
        if ("DELIVERED".equals(event.getStatus())) {
            log.info("Order delivered: {}, updating order status", event.getOrderId());
            
            // Update the order status to DELIVERED
            orderService.updateOrderStatus(event.getOrderId(), OrderStatus.DELIVERED.name())
                .subscribe(
                    order -> log.info("Order status updated to DELIVERED: {}", order),
                    error -> log.error("Error updating order status: {}", error.getMessage())
                );
        } else {
            log.info("Order not delivered properly, status: {}, no update needed", event.getStatus());
        }
    }
}