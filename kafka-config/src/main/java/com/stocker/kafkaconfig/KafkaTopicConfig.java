package com.stocker.kafkaconfig;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Topic Configuration.
 * This class provides the configuration for Kafka topics.
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    /**
     * Kafka admin client configuration.
     * @return KafkaAdmin for creating and managing Kafka topics
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    /**
     * Cart events topic.
     * @return NewTopic for cart events
     */
    @Bean
    public NewTopic cartEventsTopic() {
        return new NewTopic("cart-events", 3, (short) 1);
    }

    /**
     * Order events topic.
     * @return NewTopic for order events
     */
    @Bean
    public NewTopic orderEventsTopic() {
        return new NewTopic("order-events", 3, (short) 1);
    }

    /**
     * Payment events topic.
     * @return NewTopic for payment events
     */
    @Bean
    public NewTopic paymentEventsTopic() {
        return new NewTopic("payment-events", 3, (short) 1);
    }

    /**
     * Dispatch events topic.
     * @return NewTopic for dispatch events
     */
    @Bean
    public NewTopic dispatchEventsTopic() {
        return new NewTopic("dispatch-events", 3, (short) 1);
    }

    /**
     * Delivery events topic.
     * @return NewTopic for delivery events
     */
    @Bean
    public NewTopic deliveryEventsTopic() {
        return new NewTopic("delivery-events", 3, (short) 1);
    }
}