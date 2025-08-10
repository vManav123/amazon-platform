package com.stocker.paymentservice.kafka;

import com.stocker.common.events.payment.PaymentProcessedEvent;
import com.stocker.kafkaconfig.KafkaEventUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Kafka producer for payment events.
 * This class is responsible for sending payment-related events to Kafka topics.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaEventUtils kafkaEventUtils;
    private static final String TOPIC = "payment-events";

    /**
     * Send a PaymentProcessedEvent to the payment-events Kafka topic.
     *
     * @param event The PaymentProcessedEvent to send
     * @return Mono<Void> that completes when the event is sent
     */
    public Mono<Void> sendPaymentProcessedEvent(PaymentProcessedEvent event) {
        log.info("Sending PaymentProcessedEvent to Kafka: {}", event);
        return kafkaEventUtils.sendEvent(TOPIC, event.getPaymentId(), event)
                .then();
    }

    /**
     * Send a PaymentProcessedEvent to the payment-events Kafka topic asynchronously.
     *
     * @param event The PaymentProcessedEvent to send
     */
    public void sendPaymentProcessedEventAsync(PaymentProcessedEvent event) {
        log.info("Sending PaymentProcessedEvent to Kafka asynchronously: {}", event);
        kafkaEventUtils.sendEventAsync(TOPIC, event.getPaymentId(), event);
    }
}