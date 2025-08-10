package com.stocker.dispatchservice.kafka;

import com.stocker.common.events.dispatch.OrderDispatchedEvent;
import com.stocker.kafkaconfig.KafkaEventUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Kafka producer for dispatch events.
 * This class is responsible for sending dispatch-related events to Kafka topics.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DispatchEventProducer {

    private final KafkaEventUtils kafkaEventUtils;
    private static final String TOPIC = "dispatch-events";

    /**
     * Send an OrderDispatchedEvent to the dispatch-events Kafka topic.
     *
     * @param event The OrderDispatchedEvent to send
     * @return Mono<Void> that completes when the event is sent
     */
    public Mono<Void> sendOrderDispatchedEvent(OrderDispatchedEvent event) {
        log.info("Sending OrderDispatchedEvent to Kafka: {}", event);
        return kafkaEventUtils.sendEvent(TOPIC, event.getOrderId(), event)
                .then();
    }

    /**
     * Send an OrderDispatchedEvent to the dispatch-events Kafka topic asynchronously.
     *
     * @param event The OrderDispatchedEvent to send
     */
    public void sendOrderDispatchedEventAsync(OrderDispatchedEvent event) {
        log.info("Sending OrderDispatchedEvent to Kafka asynchronously: {}", event);
        kafkaEventUtils.sendEventAsync(TOPIC, event.getOrderId(), event);
    }
}