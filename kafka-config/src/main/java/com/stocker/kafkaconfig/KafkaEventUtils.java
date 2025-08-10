package com.stocker.kafkaconfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

/**
 * Utility class for Kafka event handling.
 * This class provides utility methods for sending and receiving Kafka events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventUtils {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Send an event to a Kafka topic.
     *
     * @param topic The topic to send the event to
     * @param key   The key for the event
     * @param event The event object
     * @return Mono<SendResult> The result of the send operation
     */
    public Mono<SendResult<String, Object>> sendEvent(String topic, String key, Object event) {
        log.info("Sending event to topic {}: {}", topic, event);
        
        return Mono.fromFuture(
            kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Event sent successfully to topic {}: {}", topic, event);
                        log.debug("Sent message with offset=[{}]", result.getRecordMetadata().offset());
                    } else {
                        log.error("Unable to send event to topic {}: {}", topic, ex.getMessage(), ex);
                    }
                })
        );
    }

    /**
     * Send an event to a Kafka topic without waiting for the result.
     *
     * @param topic The topic to send the event to
     * @param key   The key for the event
     * @param event The event object
     */
    public void sendEventAsync(String topic, String key, Object event) {
        log.info("Sending event asynchronously to topic {}: {}", topic, event);
        
        kafkaTemplate.send(topic, key, event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Event sent successfully to topic {}: {}", topic, event);
                    log.debug("Sent message with offset=[{}]", result.getRecordMetadata().offset());
                } else {
                    log.error("Unable to send event to topic {}: {}", topic, ex.getMessage(), ex);
                }
            });
    }

    /**
     * Get the topic name for a specific service's events.
     *
     * @param serviceName The name of the service
     * @return The topic name for the service's events
     */
    public String getTopicForService(String serviceName) {
        return serviceName + "-events";
    }
}