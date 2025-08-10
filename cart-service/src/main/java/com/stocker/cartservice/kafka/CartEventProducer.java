package com.stocker.cartservice.kafka;

import com.stocker.common.events.cart.CartCreatedEvent;
import com.stocker.common.events.cart.ItemAddedToCartEvent;
import com.stocker.common.events.cart.ItemRemovedFromCartEvent;
import com.stocker.kafkaconfig.KafkaEventUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Kafka producer for cart events.
 * This class is responsible for sending cart-related events to Kafka topics.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CartEventProducer {

    private final KafkaEventUtils kafkaEventUtils;
    private static final String TOPIC = "cart-events";

    /**
     * Send a CartCreatedEvent to the cart-events Kafka topic.
     *
     * @param event The CartCreatedEvent to send
     * @return Mono<Void> that completes when the event is sent
     */
    public Mono<Void> sendCartCreatedEvent(CartCreatedEvent event) {
        log.info("Sending CartCreatedEvent to Kafka: {}", event);
        return kafkaEventUtils.sendEvent(TOPIC, event.getCartId(), event)
                .then();
    }

    /**
     * Send a CartCreatedEvent to the cart-events Kafka topic asynchronously.
     *
     * @param event The CartCreatedEvent to send
     */
    public void sendCartCreatedEventAsync(CartCreatedEvent event) {
        log.info("Sending CartCreatedEvent to Kafka asynchronously: {}", event);
        kafkaEventUtils.sendEventAsync(TOPIC, event.getCartId(), event);
    }

    /**
     * Send an ItemAddedToCartEvent to the cart-events Kafka topic.
     *
     * @param event The ItemAddedToCartEvent to send
     * @return Mono<Void> that completes when the event is sent
     */
    public Mono<Void> sendItemAddedToCartEvent(ItemAddedToCartEvent event) {
        log.info("Sending ItemAddedToCartEvent to Kafka: {}", event);
        return kafkaEventUtils.sendEvent(TOPIC, event.getCartId(), event)
                .then();
    }

    /**
     * Send an ItemAddedToCartEvent to the cart-events Kafka topic asynchronously.
     *
     * @param event The ItemAddedToCartEvent to send
     */
    public void sendItemAddedToCartEventAsync(ItemAddedToCartEvent event) {
        log.info("Sending ItemAddedToCartEvent to Kafka asynchronously: {}", event);
        kafkaEventUtils.sendEventAsync(TOPIC, event.getCartId(), event);
    }

    /**
     * Send an ItemRemovedFromCartEvent to the cart-events Kafka topic.
     *
     * @param event The ItemRemovedFromCartEvent to send
     * @return Mono<Void> that completes when the event is sent
     */
    public Mono<Void> sendItemRemovedFromCartEvent(ItemRemovedFromCartEvent event) {
        log.info("Sending ItemRemovedFromCartEvent to Kafka: {}", event);
        return kafkaEventUtils.sendEvent(TOPIC, event.getCartId(), event)
                .then();
    }

    /**
     * Send an ItemRemovedFromCartEvent to the cart-events Kafka topic asynchronously.
     *
     * @param event The ItemRemovedFromCartEvent to send
     */
    public void sendItemRemovedFromCartEventAsync(ItemRemovedFromCartEvent event) {
        log.info("Sending ItemRemovedFromCartEvent to Kafka asynchronously: {}", event);
        kafkaEventUtils.sendEventAsync(TOPIC, event.getCartId(), event);
    }
}