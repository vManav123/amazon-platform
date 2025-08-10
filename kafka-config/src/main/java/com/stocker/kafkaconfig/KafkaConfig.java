package com.stocker.kafkaconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Base Kafka configuration class.
 * This class enables Kafka and provides common configuration for all services.
 */
@Configuration
@EnableKafka
public class KafkaConfig {
    // Common Kafka configuration can be added here
}