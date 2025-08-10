package com.stocker.deliveryservice.kafka;

import com.stocker.common.events.delivery.OrderDeliveredEvent;
import com.stocker.kafkaconfig.KafkaEventUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Kafka producer for delivery events.
 * This class is responsible for sending delivery-related events to Kafka topics.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventProducer {

    private final KafkaEventUtils kafkaEventUtils;
    private static final String TOPIC = "delivery-events";

    /**
     * Send an OrderDeliveredEvent to the delivery-events Kafka topic.
     *
     * @param event The OrderDeliveredEvent to send
     * @return Mono<Void> that completes when the event is sent
     */
    public Mono<Void> sendOrderDeliveredEvent(OrderDeliveredEvent event) {
        log.info("Sending OrderDeliveredEvent to Kafka: {}", event);
        return kafkaEventUtils.sendEvent(TOPIC, event.getOrderId(), event)
                .then();
    }

    /**
     * Send an OrderDeliveredEvent to the delivery-events Kafka topic asynchronously.
     *
     * @param event The OrderDeliveredEvent to send
     */
    public void sendOrderDeliveredEventAsync(OrderDeliveredEvent event) {
        log.info("Sending OrderDeliveredEvent to Kafka asynchronously: {}", event);
        kafkaEventUtils.sendEventAsync(TOPIC, event.getOrderId(), event);
    }
}