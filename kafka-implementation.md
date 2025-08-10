# Kafka Implementation in Amazon Platform

This document describes the Kafka implementation in the Amazon Platform project.

## Overview

Kafka has been implemented in the Amazon Platform project to enable event-driven communication between microservices. The implementation follows a modular approach with a dedicated `kafka-config` module that provides common Kafka configuration and utilities for all services.

## Architecture

The Kafka implementation consists of the following components:

1. **Kafka Configuration Module**: A shared module that provides common Kafka configuration and utilities.
2. **Kafka Producers**: Components that send events to Kafka topics.
3. **Kafka Consumers**: Components that receive events from Kafka topics.
4. **Event Handlers**: Components that process events received from Kafka.

## Kafka Configuration Module

The `kafka-config` module provides the following components:

- **KafkaConfig**: Base Kafka configuration class.
- **KafkaConsumerConfig**: Configuration for Kafka consumers.
- **KafkaProducerConfig**: Configuration for Kafka producers.
- **KafkaTopicConfig**: Configuration for Kafka topics.
- **KafkaEventUtils**: Utility class for sending events to Kafka topics.

## Kafka Topics

The following Kafka topics are configured:

- **cart-events**: Events related to shopping carts.
- **order-events**: Events related to orders.
- **payment-events**: Events related to payments.
- **dispatch-events**: Events related to dispatches.
- **delivery-events**: Events related to deliveries.

## Service Integration

### Dispatch Service

The Dispatch Service integrates with Kafka through:

1. **DispatchEventProducer**: Sends OrderDispatchedEvent to the dispatch-events topic.
2. **DispatchEventConsumer**: Listens for OrderCreatedEvent and PaymentProcessedEvent from the order-events and payment-events topics.
3. **DispatchService**: Processes dispatches and sends events to Kafka.

### Delivery Service

The Delivery Service integrates with Kafka through:

1. **DeliveryEventProducer**: Sends OrderDeliveredEvent to the delivery-events topic.
2. **DeliveryEventConsumer**: Listens for OrderDispatchedEvent from the dispatch-events topic.
3. **DeliveryService**: Processes deliveries and sends events to Kafka.

## Event Flow

The event flow in the system is as follows:

1. An order is created, and an OrderCreatedEvent is sent to the order-events topic.
2. The payment service processes the payment and sends a PaymentProcessedEvent to the payment-events topic.
3. The dispatch service listens for PaymentProcessedEvent, processes the dispatch, and sends an OrderDispatchedEvent to the dispatch-events topic.
4. The delivery service listens for OrderDispatchedEvent, processes the delivery, and sends an OrderDeliveredEvent to the delivery-events topic.
5. The order service listens for OrderDeliveredEvent and updates the order status.

## Configuration

Kafka is configured in each service's `application.properties` file with the following properties:

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=<service-name>-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=com.stocker.*
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.producer.properties.spring.json.add.type.headers=false
```

## Integration with Axon Framework

The project uses Axon Framework for implementing the SAGA pattern and CQRS. While Axon Framework has its own event sourcing capabilities, Kafka is used as a complementary messaging system for inter-service communication.

## Testing

To test the Kafka implementation:

1. Start a Kafka broker using Docker or a local installation.
2. Start all services in the Amazon Platform.
3. Create an order through the order service API.
4. Verify that events are properly published and consumed by checking the logs of each service.

## Future Enhancements

Possible future enhancements to the Kafka implementation:

1. Implement Kafka Streams for complex event processing.
2. Add dead letter queues for handling failed messages.
3. Implement retry mechanisms for failed message processing.
4. Add monitoring and alerting for Kafka topics and consumers.
5. Configure Axon Framework to use Kafka as the event store.