# Kafka Implementation Summary

This document summarizes the Kafka implementation in the Amazon Platform project.

## Changes Made

The following changes were made to implement Kafka in the project:

1. **Created PaymentEventProducer**: Implemented a Kafka producer for the Payment service to send payment-related events to the "payment-events" topic.
2. **Created PaymentEventConsumer**: Implemented a Kafka consumer for the Payment service to receive order-related events from the "order-events" topic.
3. **Updated PaymentService Interface**: Modified the `processPayment` method to include a `paymentId` parameter for consistency with other services.
4. **Updated PaymentServiceImpl**: Modified the implementation to use the `PaymentEventProducer` to send `PaymentProcessedEvent` after processing a payment.

## Implementation Details

### Kafka Configuration

The project uses a dedicated `kafka-config` module that provides common Kafka configuration and utilities for all services:

- **KafkaConfig**: Base Kafka configuration class with `@EnableKafka` annotation.
- **KafkaConsumerConfig**: Configuration for Kafka consumers, including deserializers and consumer properties.
- **KafkaProducerConfig**: Configuration for Kafka producers, including serializers and producer properties.
- **KafkaTopicConfig**: Configuration for Kafka topics, creating topics for each service.
- **KafkaEventUtils**: Utility class for sending events to Kafka topics, with both synchronous and asynchronous methods.

### Kafka Topics

The following Kafka topics are configured:

- **cart-events**: Events related to shopping carts.
- **order-events**: Events related to orders.
- **payment-events**: Events related to payments.
- **dispatch-events**: Events related to dispatches.
- **delivery-events**: Events related to deliveries.

### Service Integration

Each service integrates with Kafka through:

1. **Event Producers**: Send service-specific events to Kafka topics.
2. **Event Consumers**: Listen for events from other services' topics.
3. **Service Implementation**: Process events and trigger business logic.

## Event Flow

The event flow in the system is as follows:

1. An order is created, and an OrderCreatedEvent is sent to the order-events topic.
2. The payment service listens for OrderCreatedEvent, processes the payment, and sends a PaymentProcessedEvent to the payment-events topic.
3. The dispatch service listens for PaymentProcessedEvent, processes the dispatch, and sends an OrderDispatchedEvent to the dispatch-events topic.
4. The delivery service listens for OrderDispatchedEvent, processes the delivery, and sends an OrderDeliveredEvent to the delivery-events topic.
5. The order service listens for OrderDeliveredEvent and updates the order status.

## Testing the Implementation

To test the Kafka implementation:

1. Start a Kafka broker using Docker:
   ```bash
   docker run -d --name kafka -p 9092:9092 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 wurstmeister/kafka
   ```

2. Start all services in the Amazon Platform:
   ```bash
   ./mvnw spring-boot:run -pl order-service
   ./mvnw spring-boot:run -pl payment-service
   ./mvnw spring-boot:run -pl dispatch-service
   ./mvnw spring-boot:run -pl delivery-service
   ./mvnw spring-boot:run -pl cart-service
   ```

3. Create an order through the order service API:
   ```bash
   curl -X POST http://localhost:8082/api/orders -H "Content-Type: application/json" -d '{"customerId":"customer123","items":[{"productId":"product123","quantity":1,"price":100.00}]}'
   ```

4. Verify that events are properly published and consumed by checking the logs of each service.

## Future Enhancements

Possible future enhancements to the Kafka implementation:

1. Implement Kafka Streams for complex event processing.
2. Add dead letter queues for handling failed messages.
3. Implement retry mechanisms for failed message processing.
4. Add monitoring and alerting for Kafka topics and consumers.
5. Configure Axon Framework to use Kafka as the event store.