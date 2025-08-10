package com.stocker.orderservice.kafka;

import com.stocker.common.events.order.OrderCreatedEvent;
import com.stocker.common.events.order.OrderStatusUpdatedEvent;
import com.stocker.kafkaconfig.KafkaEventUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Kafka producer for order events.
 * This class is responsible for sending order-related events to Kafka topics.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaEventUtils kafkaEventUtils;
    private static final String TOPIC = "order-events";

    /**
     * Send an OrderCreatedEvent to the order-events Kafka topic.
     *
     * @param event The OrderCreatedEvent to send
     * @return Mono<Void> that completes when the event is sent
     */
    public Mono<Void> sendOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Sending OrderCreatedEvent to Kafka: {}", event);
        return kafkaEventUtils.sendEvent(TOPIC, event.getOrderId(), event)
                .then();
    }

    /**
     * Send an OrderCreatedEvent to the order-events Kafka topic asynchronously.
     *
     * @param event The OrderCreatedEvent to send
     */
    public void sendOrderCreatedEventAsync(OrderCreatedEvent event) {
        log.info("Sending OrderCreatedEvent to Kafka asynchronously: {}", event);
        kafkaEventUtils.sendEventAsync(TOPIC, event.getOrderId(), event);
    }

    /**
     * Send an OrderStatusUpdatedEvent to the order-events Kafka topic.
     *
     * @param event The OrderStatusUpdatedEvent to send
     * @return Mono<Void> that completes when the event is sent
     */
    public Mono<Void> sendOrderStatusUpdatedEvent(OrderStatusUpdatedEvent event) {
        log.info("Sending OrderStatusUpdatedEvent to Kafka: {}", event);
        return kafkaEventUtils.sendEvent(TOPIC, event.getOrderId(), event)
                .then();
    }

    /**
     * Send an OrderStatusUpdatedEvent to the order-events Kafka topic asynchronously.
     *
     * @param event The OrderStatusUpdatedEvent to send
     */
    public void sendOrderStatusUpdatedEventAsync(OrderStatusUpdatedEvent event) {
        log.info("Sending OrderStatusUpdatedEvent to Kafka asynchronously: {}", event);
        kafkaEventUtils.sendEventAsync(TOPIC, event.getOrderId(), event);
    }
}