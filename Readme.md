# Amazon Platform Project Documentation

### Project Overview

The Amazon Platform is a microservices-based e-commerce application that implements the SAGA and CQRS (Command Query Responsibility Segregation) patterns. It's built using Spring Boot and Axon Framework, with reactive programming support through Spring WebFlux and R2DBC. The platform simulates a shopping experience similar to Amazon, with services for cart management, order processing, payment handling, dispatch, and delivery.

### Architecture

The project follows a microservices architecture with the following components:

1. **Cart Service**: Manages shopping carts and cart items
2. **Order Service**: Handles order creation and management
3. **Payment Service**: Processes payments for orders
4. **Dispatch Service**: Manages order dispatching
5. **Delivery Service**: Handles order delivery
6. **Common Module**: Contains shared domain models, commands, and events
7. **Kafka Config Module**: Provides Kafka configuration for event-driven communication

#### High-Level Architecture Diagram

The following diagram illustrates the complete architecture of the Amazon Platform:

```mermaid
graph TD
    %% Client and API Gateway
    Client[Client Applications]
    Gateway[API Gateway]
    
    %% Microservices
    CartService[Cart Service]
    OrderService[Order Service]
    PaymentService[Payment Service]
    DispatchService[Dispatch Service]
    DeliveryService[Delivery Service]
    
    %% Databases
    CartDB[(Cart DB)]
    OrderDB[(Order DB)]
    PaymentDB[(Payment DB)]
    DispatchDB[(Dispatch DB)]
    DeliveryDB[(Delivery DB)]
    
    %% Kafka Event Bus
    Kafka[Kafka Event Bus]
    
    %% CQRS Components for Cart Service
    CartCommandAPI[Cart Command API]
    CartEventHandlers[Cart Event Handlers]
    CartQueryAPI[Cart Query API]
    
    %% CQRS Components for Order Service
    OrderCommandAPI[Order Command API]
    OrderEventHandlers[Order Event Handlers]
    OrderQueryAPI[Order Query API]
    
    %% CQRS Components for Payment Service
    PaymentCommandAPI[Payment Command API]
    PaymentEventHandlers[Payment Event Handlers]
    PaymentQueryAPI[Payment Query API]
    
    %% CQRS Components for Dispatch Service
    DispatchCommandAPI[Dispatch Command API]
    DispatchEventHandlers[Dispatch Event Handlers]
    DispatchQueryAPI[Dispatch Query API]
    
    %% CQRS Components for Delivery Service
    DeliveryCommandAPI[Delivery Command API]
    DeliveryEventHandlers[Delivery Event Handlers]
    DeliveryQueryAPI[Delivery Query API]
    
    %% SAGA Orchestration
    OrderSaga[Order Saga Orchestration]
    
    %% Kafka Topics
    CartEvents[cart-events]
    OrderEvents[order-events]
    PaymentEvents[payment-events]
    DispatchEvents[dispatch-events]
    DeliveryEvents[delivery-events]
    
    %% Client to API Gateway
    Client --> Gateway
    
    %% API Gateway to Services
    Gateway --> CartService
    Gateway --> OrderService
    Gateway --> PaymentService
    Gateway --> DispatchService
    Gateway --> DeliveryService
    
    %% Cart Service CQRS
    CartService --> CartCommandAPI
    CartService --> CartEventHandlers
    CartService --> CartQueryAPI
    CartCommandAPI --> CartEventHandlers
    CartEventHandlers --> CartQueryAPI
    CartService --> CartDB
    
    %% Order Service CQRS
    OrderService --> OrderCommandAPI
    OrderService --> OrderEventHandlers
    OrderService --> OrderQueryAPI
    OrderCommandAPI --> OrderEventHandlers
    OrderEventHandlers --> OrderQueryAPI
    OrderService --> OrderDB
    
    %% Payment Service CQRS
    PaymentService --> PaymentCommandAPI
    PaymentService --> PaymentEventHandlers
    PaymentService --> PaymentQueryAPI
    PaymentCommandAPI --> PaymentEventHandlers
    PaymentEventHandlers --> PaymentQueryAPI
    PaymentService --> PaymentDB
    
    %% Dispatch Service CQRS
    DispatchService --> DispatchCommandAPI
    DispatchService --> DispatchEventHandlers
    DispatchService --> DispatchQueryAPI
    DispatchCommandAPI --> DispatchEventHandlers
    DispatchEventHandlers --> DispatchQueryAPI
    DispatchService --> DispatchDB
    
    %% Delivery Service CQRS
    DeliveryService --> DeliveryCommandAPI
    DeliveryService --> DeliveryEventHandlers
    DeliveryService --> DeliveryQueryAPI
    DeliveryCommandAPI --> DeliveryEventHandlers
    DeliveryEventHandlers --> DeliveryQueryAPI
    DeliveryService --> DeliveryDB
    
    %% Kafka Event Bus
    Kafka --> CartEvents
    Kafka --> OrderEvents
    Kafka --> PaymentEvents
    Kafka --> DispatchEvents
    Kafka --> DeliveryEvents
    
    %% Services to Kafka
    CartEventHandlers --> Kafka
    OrderEventHandlers --> Kafka
    PaymentEventHandlers --> Kafka
    DispatchEventHandlers --> Kafka
    DeliveryEventHandlers --> Kafka
    
    %% SAGA Orchestration
    OrderService --> OrderSaga
    OrderSaga --> OrderService
    OrderSaga --> PaymentService
    OrderSaga --> DispatchService
    OrderSaga --> DeliveryService
    
    %% Styling
    classDef service fill:#f9f,stroke:#333,stroke-width:2px
    classDef database fill:#bbf,stroke:#333,stroke-width:2px
    classDef api fill:#bfb,stroke:#333,stroke-width:2px
    classDef eventBus fill:#fbb,stroke:#333,stroke-width:2px
    classDef saga fill:#fbf,stroke:#333,stroke-width:2px
    
    class CartService,OrderService,PaymentService,DispatchService,DeliveryService service
    class CartDB,OrderDB,PaymentDB,DispatchDB,DeliveryDB database
    class CartCommandAPI,CartQueryAPI,OrderCommandAPI,OrderQueryAPI,PaymentCommandAPI,PaymentQueryAPI,DispatchCommandAPI,DispatchQueryAPI,DeliveryCommandAPI,DeliveryQueryAPI api
    class Kafka,CartEvents,OrderEvents,PaymentEvents,DispatchEvents,DeliveryEvents eventBus
    class OrderSaga saga
```

This diagram illustrates:

1. **CQRS Pattern**: Each service has separate Command and Query APIs
2. **Event-Driven Architecture**: Services communicate through the Kafka Event Bus
3. **Database per Service**: Each microservice has its own database
4. **API Gateway**: Entry point for client applications
5. **Microservices**: Cart, Order, Payment, Dispatch, and Delivery services

Each service has its own database and communicates with other services through events published to Kafka topics. The Order Service implements the SAGA pattern to coordinate the entire order processing workflow across multiple services.

### Technology Stack

- **Java 17**: Core programming language
- **Spring Boot 3.5.4**: Application framework
- **Spring WebFlux**: Reactive web framework
- **Axon Framework 4.9.3**: CQRS and Event Sourcing framework
- **R2DBC**: Reactive database connectivity
- **PostgreSQL**: Primary database
- **Kafka**: Event streaming platform
- **Project Lombok**: Reduces boilerplate code
- **Maven**: Build and dependency management

### Design Patterns

#### CQRS (Command Query Responsibility Segregation)
The application separates command operations (write) from query operations (read):
- Commands: Used to modify state (e.g., CreateCartCommand, AddItemToCartCommand)
- Queries: Used to retrieve data
- Events: Represent state changes (e.g., CartCreatedEvent, ItemAddedToCartEvent)

##### CQRS Pattern Sequence Diagram

The following diagram illustrates how the system implements the CQRS pattern across multiple services:

```mermaid
sequenceDiagram
    participant Client
    participant CartCommandAPI as Cart Service<br/>Command API
    participant CartEventHandler as Cart Service<br/>Event Handler
    participant CartQueryAPI as Cart Service<br/>Query API
    participant OrderCommandAPI as Order Service<br/>Command API
    participant OrderEventHandler as Order Service<br/>Event Handler
    participant OrderQueryAPI as Order Service<br/>Query API
    participant OrderSaga as Order Saga
    participant Kafka as Kafka Event Bus
    
    %% Cart Creation - Command Side
    Client->>CartCommandAPI: CreateCartCommand
    CartCommandAPI->>CartCommandAPI: Validate Command
    CartCommandAPI->>CartCommandAPI: Create Cart Entity
    CartCommandAPI->>Kafka: CartCreatedEvent
    
    %% Cart Creation - Event Handling
    Kafka->>CartEventHandler: CartCreatedEvent
    CartEventHandler->>CartEventHandler: Update View Model
    
    %% Cart Query - Query Side
    Client->>CartQueryAPI: Query Cart
    CartQueryAPI->>CartQueryAPI: Read View Model
    CartQueryAPI-->>Client: Cart Response
    
    %% Add Item to Cart - Command Side
    Client->>CartCommandAPI: AddItemToCartCommand
    CartCommandAPI->>CartCommandAPI: Validate Command
    CartCommandAPI->>CartCommandAPI: Update Cart Entity
    CartCommandAPI->>Kafka: ItemAddedToCartEvent
    
    %% Add Item to Cart - Event Handling
    Kafka->>CartEventHandler: ItemAddedToCartEvent
    CartEventHandler->>CartEventHandler: Update View Model
    
    %% Cart Query - Query Side
    Client->>CartQueryAPI: Query Updated Cart
    CartQueryAPI->>CartQueryAPI: Read View Model
    CartQueryAPI-->>Client: Updated Cart Response
    
    %% Create Order - Command Side
    Client->>OrderCommandAPI: CreateOrderCommand
    OrderCommandAPI->>OrderCommandAPI: Validate Command
    OrderCommandAPI->>OrderCommandAPI: Create Order Entity
    OrderCommandAPI->>Kafka: OrderCreatedEvent
    
    %% Create Order - Event Handling
    Kafka->>OrderEventHandler: OrderCreatedEvent
    OrderEventHandler->>OrderEventHandler: Update View Model
    Kafka->>OrderSaga: OrderCreatedEvent
    
    %% SAGA Orchestration (simplified)
    Note over OrderSaga: SAGA Orchestration<br/>(Payment, Dispatch, Delivery)
    
    %% Order Status Updates - Event Handling
    OrderSaga->>OrderCommandAPI: UpdateOrderStatusCommand (PAID)
    OrderCommandAPI->>OrderCommandAPI: Update Order Entity
    OrderCommandAPI->>Kafka: OrderStatusUpdatedEvent (PAID)
    Kafka->>OrderEventHandler: OrderStatusUpdatedEvent (PAID)
    OrderEventHandler->>OrderEventHandler: Update View Model
    
    OrderSaga->>OrderCommandAPI: UpdateOrderStatusCommand (DISPATCHED)
    OrderCommandAPI->>OrderCommandAPI: Update Order Entity
    OrderCommandAPI->>Kafka: OrderStatusUpdatedEvent (DISPATCHED)
    Kafka->>OrderEventHandler: OrderStatusUpdatedEvent (DISPATCHED)
    OrderEventHandler->>OrderEventHandler: Update View Model
    
    OrderSaga->>OrderCommandAPI: UpdateOrderStatusCommand (DELIVERED)
    OrderCommandAPI->>OrderCommandAPI: Update Order Entity
    OrderCommandAPI->>Kafka: OrderStatusUpdatedEvent (DELIVERED)
    Kafka->>OrderEventHandler: OrderStatusUpdatedEvent (DELIVERED)
    OrderEventHandler->>OrderEventHandler: Update View Model
    
    %% Order Query - Query Side
    Client->>OrderQueryAPI: Query Order
    OrderQueryAPI->>OrderQueryAPI: Read View Model
    OrderQueryAPI-->>Client: Order Response
    
    %% Multiple Clients Querying - Demonstrating CQRS Benefits
    Note over Client,Kafka: Multiple Clients Querying
    
    Client->>CartQueryAPI: Query Cart
    CartQueryAPI->>CartQueryAPI: Read View Model
    CartQueryAPI-->>Client: Cart Response
    
    Client->>OrderQueryAPI: Query Order
    OrderQueryAPI->>OrderQueryAPI: Read View Model
    OrderQueryAPI-->>Client: Order Response
    
    Note over Client,Kafka: Simultaneous Command Processing
    
    Client->>CartCommandAPI: AddItemToCartCommand
    CartCommandAPI->>CartCommandAPI: Validate Command
    CartCommandAPI->>CartCommandAPI: Update Cart Entity
    CartCommandAPI->>Kafka: ItemAddedToCartEvent
```

##### CQRS Pattern Implementation Details

This diagram illustrates how the Cart Service implements the CQRS pattern:

1. **Command-Query Separation**:
   - **Command Side**: Handles commands that modify state (CreateCartCommand, AddItemToCartCommand)
   - **Query Side**: Handles queries that read state but don't modify it

2. **Event Sourcing**:
   - Commands result in events (CartCreatedEvent, ItemAddedToCartEvent)
   - Events represent state changes and are the source of truth
   - Events are published to Kafka Event Bus

3. **Materialized Views**:
   - Query side maintains optimized read models (views)
   - Views are updated based on events
   - Provides efficient querying capabilities

4. **Eventual Consistency**:
   - Other services consume events to maintain their own view of cart data
   - System becomes eventually consistent as events propagate

#### SAGA Pattern
The OrderSaga class orchestrates the order processing workflow across multiple services:
1. Order creation
2. Payment processing
3. Order dispatching
4. Order delivery

The saga includes compensating transactions to handle failures at any step, ensuring data consistency across services.

##### Order Processing Sequence Diagram (SAGA Pattern)

The following diagram illustrates the complete end-to-end flow from cart creation to order delivery using the SAGA pattern:

```mermaid
sequenceDiagram
    participant Client
    participant CartService as Cart Service
    participant OrderService as Order Service
    participant OrderSaga as Order Saga
    participant PaymentService as Payment Service
    participant DispatchService as Dispatch Service
    participant DeliveryService as Delivery Service
    participant Kafka as Kafka Event Bus

    %% Cart Creation and Management
    Client->>CartService: CreateCartCommand
    CartService->>CartService: Validate & Create Cart
    CartService->>Kafka: CartCreatedEvent
    
    Client->>CartService: AddItemToCartCommand
    CartService->>CartService: Validate & Add Item
    CartService->>Kafka: ItemAddedToCartEvent
    
    Client->>CartService: Query Cart
    CartService-->>Client: Cart Details
    
    %% Order Creation
    Client->>OrderService: CreateOrderCommand (cartId, userId, shippingAddress)
    OrderService->>OrderService: Validate & Create Order
    OrderService->>Kafka: OrderCreatedEvent
    Kafka->>OrderSaga: OrderCreatedEvent
    
    %% Payment Processing
    OrderSaga->>PaymentService: ProcessPaymentCommand
    PaymentService->>PaymentService: Process Payment
    PaymentService->>Kafka: PaymentProcessedEvent (COMPLETED)
    Kafka->>OrderSaga: PaymentProcessedEvent
    
    %% Update Order Status - PAID
    OrderSaga->>OrderService: UpdateOrderStatusCommand (PAID)
    OrderService->>OrderService: Update Order Status
    OrderService->>Kafka: OrderStatusUpdatedEvent (PAID)
    
    %% Order Dispatch
    OrderSaga->>DispatchService: DispatchOrderCommand
    DispatchService->>DispatchService: Process Dispatch
    DispatchService->>Kafka: OrderDispatchedEvent
    Kafka->>OrderSaga: OrderDispatchedEvent
    
    %% Update Order Status - DISPATCHED
    OrderSaga->>OrderService: UpdateOrderStatusCommand (DISPATCHED)
    OrderService->>OrderService: Update Order Status
    OrderService->>Kafka: OrderStatusUpdatedEvent (DISPATCHED)
    
    %% Order Delivery
    OrderSaga->>DeliveryService: DeliverOrderCommand
    DeliveryService->>DeliveryService: Process Delivery
    DeliveryService->>Kafka: OrderDeliveredEvent
    Kafka->>OrderSaga: OrderDeliveredEvent
    
    %% Update Order Status - DELIVERED
    OrderSaga->>OrderService: UpdateOrderStatusCommand (DELIVERED)
    OrderService->>OrderService: Update Order Status
    OrderService->>Kafka: OrderStatusUpdatedEvent (DELIVERED)
    
    %% Query Order Status
    Client->>OrderService: Query Order
    OrderService-->>Client: Order Details (DELIVERED)
```

##### Error Handling and Compensation Transactions

The following diagram illustrates how the system handles failures and implements compensating transactions:

```mermaid
sequenceDiagram
    participant Client
    participant OrderService as Order Service
    participant OrderSaga as Order Saga
    participant PaymentService as Payment Service
    participant DispatchService as Dispatch Service
    participant Kafka as Kafka Event Bus

    %% Order Creation
    Client->>OrderService: CreateOrderCommand
    OrderService->>OrderService: Validate & Create Order
    OrderService->>Kafka: OrderCreatedEvent
    Kafka->>OrderSaga: OrderCreatedEvent
    
    %% Payment Processing - Failure Scenario
    OrderSaga->>PaymentService: ProcessPaymentCommand
    PaymentService->>PaymentService: Process Payment
    Note over PaymentService: Payment Fails (e.g., insufficient funds)
    PaymentService->>Kafka: PaymentProcessedEvent (FAILED)
    Kafka->>OrderSaga: PaymentProcessedEvent (FAILED)
    
    %% Compensation Transaction - Payment Failure
    OrderSaga->>OrderService: UpdateOrderStatusCommand (PAYMENT_FAILED)
    OrderService->>OrderService: Update Order Status
    OrderService->>Kafka: OrderStatusUpdatedEvent (PAYMENT_FAILED)
    Note over OrderSaga: End Saga
    
    %% Client Queries Failed Order
    Client->>OrderService: Query Order
    OrderService-->>Client: Order Details (PAYMENT_FAILED)
```

##### Dispatch Failure Handling

The following diagram illustrates how the system handles dispatch failures:

```mermaid
sequenceDiagram
    participant OrderService as Order Service
    participant OrderSaga as Order Saga
    participant PaymentService as Payment Service
    participant DispatchService as Dispatch Service
    participant Kafka as Kafka Event Bus
    
    %% Payment Success
    PaymentService->>Kafka: PaymentProcessedEvent (COMPLETED)
    Kafka->>OrderSaga: PaymentProcessedEvent (COMPLETED)
    
    %% Update Order Status - PAID
    OrderSaga->>OrderService: UpdateOrderStatusCommand (PAID)
    OrderService->>OrderService: Update Order Status
    OrderService->>Kafka: OrderStatusUpdatedEvent (PAID)
    
    %% Dispatch Failure
    OrderSaga->>DispatchService: DispatchOrderCommand
    DispatchService->>DispatchService: Process Dispatch
    Note over DispatchService: Dispatch Fails (e.g., item out of stock)
    DispatchService->>Kafka: OrderDispatchedEvent (FAILED)
    Kafka->>OrderSaga: OrderDispatchedEvent (FAILED)
    
    %% Compensation Transaction - Dispatch Failure
    OrderSaga->>OrderService: UpdateOrderStatusCommand (DISPATCH_FAILED)
    OrderService->>OrderService: Update Order Status
    OrderService->>Kafka: OrderStatusUpdatedEvent (DISPATCH_FAILED)
    Note over OrderSaga: End Saga
```

##### System Error Handling

The following diagram illustrates how the system handles system exceptions during saga execution:

```mermaid
sequenceDiagram
    participant Client
    participant OrderService as Order Service
    participant OrderSaga as Order Saga
    participant PaymentService as Payment Service
    participant Kafka as Kafka Event Bus
    
    %% Order Creation
    Client->>OrderService: CreateOrderCommand
    OrderService->>OrderService: Validate & Create Order
    OrderService->>Kafka: OrderCreatedEvent
    Kafka->>OrderSaga: OrderCreatedEvent
    
    %% Exception during Saga Execution
    OrderSaga->>PaymentService: ProcessPaymentCommand
    Note over OrderSaga: System Exception
    
    %% Error Handling
    OrderSaga->>OrderService: UpdateOrderStatusCommand (ERROR)
    OrderService->>OrderService: Update Order Status
    OrderService->>Kafka: OrderStatusUpdatedEvent (ERROR)
    Note over OrderSaga: End Saga
    
    %% Client Queries Failed Order
    Client->>OrderService: Query Order
    OrderService-->>Client: Order Details (ERROR)
```

The SAGA pattern implementation includes:

1. **Orchestration**: The OrderSaga class orchestrates the entire workflow
2. **Sequential Steps**: Order creation → Payment → Dispatch → Delivery
3. **Event-Driven Communication**: Services communicate through events
4. **Compensation Transactions**: In case of failures, compensating actions maintain data consistency:
   - If payment fails: Order status is updated to PAYMENT_FAILED
   - If dispatch fails: Order status is updated to DISPATCH_FAILED
   - If delivery fails: Order status is updated to DELIVERY_FAILED
5. **Saga State Management**: The saga maintains state throughout the transaction

#### Complete End-to-End Flow Diagram

The following diagram illustrates the complete end-to-end flow from cart creation to order delivery, showing the interaction between all services:

```mermaid
sequenceDiagram
    participant Client
    participant CartService as Cart Service
    participant OrderService as Order Service
    participant OrderSaga as Order Saga
    participant PaymentService as Payment Service
    participant DispatchService as Dispatch Service
    participant DeliveryService as Delivery Service
    participant Kafka as Kafka Event Bus

    %% Cart Creation and Management
    Client->>CartService: 1. Create Cart and Add Items
    CartService->>CartService: Process Cart Commands
    
    Client->>CartService: 2. Query Cart
    CartService-->>Client: Cart Response
    
    %% Order Creation
    Client->>OrderService: 3. Create Order (cartId, userId, shippingAddress)
    OrderService->>OrderService: Validate & Create Order
    OrderService->>Kafka: OrderCreatedEvent
    Kafka->>OrderSaga: OrderCreatedEvent
    
    %% Payment Processing
    OrderSaga->>PaymentService: 4. Process Payment
    PaymentService->>PaymentService: Process Payment
    PaymentService->>Kafka: PaymentProcessedEvent
    Kafka->>OrderSaga: PaymentProcessedEvent
    
    %% Update Order Status - PAID
    OrderSaga->>OrderService: 5. Update Order Status (PAID)
    OrderService->>OrderService: Update Order Status
    OrderService->>Kafka: OrderStatusUpdatedEvent (PAID)
    
    %% Order Dispatch
    OrderSaga->>DispatchService: 6. Dispatch Order
    DispatchService->>DispatchService: Process Dispatch
    DispatchService->>Kafka: OrderDispatchedEvent
    Kafka->>OrderSaga: OrderDispatchedEvent
    
    %% Update Order Status - DISPATCHED
    OrderSaga->>OrderService: 7. Update Order Status (DISPATCHED)
    OrderService->>OrderService: Update Order Status
    OrderService->>Kafka: OrderStatusUpdatedEvent (DISPATCHED)
    
    %% Order Delivery
    OrderSaga->>DeliveryService: 8. Deliver Order
    DeliveryService->>DeliveryService: Process Delivery
    DeliveryService->>Kafka: OrderDeliveredEvent
    Kafka->>OrderSaga: OrderDeliveredEvent
    
    %% Update Order Status - DELIVERED
    OrderSaga->>OrderService: 9. Update Order Status (DELIVERED)
    OrderService->>OrderService: Update Order Status
    OrderService->>Kafka: OrderStatusUpdatedEvent (DELIVERED)
    
    %% Query Order Status
    Client->>OrderService: 10. Query Order Status
    OrderService-->>Client: Order Status Response
```

This end-to-end flow diagram illustrates:

1. **Cart Management**: The client creates a cart and adds items through the Cart Service
2. **Order Creation**: The client creates an order using the items in the cart
3. **SAGA Orchestration**: The OrderSaga orchestrates the entire order processing workflow
4. **Payment Processing**: The Payment Service processes the payment for the order
5. **Order Dispatching**: The Dispatch Service handles the order dispatch
6. **Order Delivery**: The Delivery Service manages the order delivery
7. **Status Updates**: The Order Service updates the order status at each step
8. **Query Operations**: The client can query the cart and order status at any point

The diagram shows how the CQRS and SAGA patterns work together to provide a complete e-commerce solution with clear separation of concerns and reliable transaction management across multiple services.

### Database Configuration

The project uses PostgreSQL with reactive support through R2DBC:

- Each microservice has its own database:
  - cart_db
  - payment_db
  - order_db
  - dispatch_db
  - delivery_db

A setup script (`setup-postgres.sh`) is provided to create these databases.

### Kafka Implementation

Kafka is used for event-driven communication between microservices:

#### Kafka Topics
- **cart-events**: Events related to shopping carts
- **order-events**: Events related to orders
- **payment-events**: Events related to payments
- **dispatch-events**: Events related to dispatches
- **delivery-events**: Events related to deliveries

#### Kafka Event Flow Diagram

The following diagram illustrates the event flow between services through Kafka:

```mermaid
sequenceDiagram
    participant CartService as Cart Service
    participant OrderService as Order Service
    participant PaymentService as Payment Service
    participant DispatchService as Dispatch Service
    participant DeliveryService as Delivery Service
    participant CartEvents as cart-events topic
    participant OrderEvents as order-events topic
    participant PaymentEvents as payment-events topic
    participant DispatchEvents as dispatch-events topic
    participant DeliveryEvents as delivery-events topic
    
    %% Cart Events
    CartService->>CartEvents: CartCreatedEvent
    CartService->>CartEvents: ItemAddedToCartEvent
    CartService->>CartEvents: ItemRemovedFromCartEvent
    
    %% Order Events
    OrderService->>OrderEvents: OrderCreatedEvent
    OrderEvents->>PaymentService: OrderCreatedEvent
    OrderService->>OrderEvents: OrderStatusUpdatedEvent
    
    %% Payment Events
    PaymentService->>PaymentEvents: PaymentProcessedEvent
    PaymentEvents->>OrderService: PaymentProcessedEvent
    PaymentEvents->>DispatchService: PaymentProcessedEvent (COMPLETED)
    
    %% Dispatch Events
    DispatchService->>DispatchEvents: OrderDispatchedEvent
    DispatchEvents->>OrderService: OrderDispatchedEvent
    DispatchEvents->>DeliveryService: OrderDispatchedEvent
    
    %% Delivery Events
    DeliveryService->>DeliveryEvents: OrderDeliveredEvent
    DeliveryEvents->>OrderService: OrderDeliveredEvent
    
    %% Order Status Updates
    Note over OrderService: Updates Order Status based on events
```

#### Event Flow
1. An order is created, and an OrderCreatedEvent is sent to the order-events topic
2. The payment service processes the payment and sends a PaymentProcessedEvent to the payment-events topic
3. The dispatch service processes the dispatch and sends an OrderDispatchedEvent to the dispatch-events topic
4. The delivery service processes the delivery and sends an OrderDeliveredEvent to the delivery-events topic
5. The order service updates the order status based on these events

### Service Details

#### Cart Service
- Manages shopping carts and cart items
- Handles commands: CreateCartCommand, AddItemToCartCommand, RemoveItemFromCartCommand
- Publishes events: CartCreatedEvent, ItemAddedToCartEvent, ItemRemovedFromCartEvent

#### Order Service
- Manages orders and order items
- Implements the OrderSaga to coordinate the order process
- Handles commands: CreateOrderCommand, UpdateOrderStatusCommand
- Publishes events: OrderCreatedEvent, OrderStatusUpdatedEvent

#### Payment Service
- Processes payments for orders
- Handles commands: ProcessPaymentCommand
- Publishes events: PaymentProcessedEvent

#### Dispatch Service
- Manages order dispatching
- Handles commands: DispatchOrderCommand
- Publishes events: OrderDispatchedEvent

#### Delivery Service
- Manages order delivery
- Handles commands: DeliverOrderCommand
- Publishes events: OrderDeliveredEvent

### Setup and Running the Application

#### Prerequisites
- Java 17
- Maven
- PostgreSQL
- Kafka

#### Setup Steps
1. Clone the repository
2. Run the PostgreSQL setup script:
   ```bash
   ./setup-postgres.sh
   ```
3. Start Kafka:
   ```bash
   docker run -d --name kafka -p 9092:9092 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 wurstmeister/kafka
   ```
4. Build the project:
   ```bash
   ./mvnw clean install
   ```
5. Start each service:
   ```bash
   ./mvnw spring-boot:run -pl cart-service
   ./mvnw spring-boot:run -pl order-service
   ./mvnw spring-boot:run -pl payment-service
   ./mvnw spring-boot:run -pl dispatch-service
   ./mvnw spring-boot:run -pl delivery-service
   ```

#### Testing
A test script (`test-amazon-platform.sh`) is provided to simulate the entire order flow:
```bash
./test-amazon-platform.sh
```

### Future Enhancements

1. **Kafka Streams**: Implement for complex event processing
2. **Dead Letter Queues**: Add for handling failed messages
3. **Retry Mechanisms**: Implement for failed message processing
4. **Monitoring and Alerting**: Add for Kafka topics and consumers
5. **Axon with Kafka**: Configure Axon Framework to use Kafka as the event store

### Low-Level Implementation Diagrams

This section provides detailed low-level diagrams showing the internal implementation of each service.

#### Cart Service Class Diagram

The following diagram illustrates the class structure and relationships within the Cart Service:

```mermaid
classDiagram
    %% Domain Classes
    class Cart {
        -String cartId
        -String userId
        -Map~String, CartItem~ items
        +handle(CreateCartCommand)
        +handle(AddItemToCartCommand)
        +handle(RemoveItemFromCartCommand)
        +on(CartCreatedEvent)
        +on(ItemAddedToCartEvent)
        +on(ItemRemovedFromCartEvent)
    }
    
    class CartItem {
        -String cartItemId
        -String productId
        -String productName
        -int quantity
        -BigDecimal unitPrice
        +CartItem(cartItemId, productId, productName, quantity, unitPrice)
        +getSubtotal()
    }
    
    %% Repository Entities
    class CartEntity {
        -String cartId
        -String userId
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
    }
    
    class CartItemEntity {
        -String cartItemId
        -String cartId
        -String productId
        -String productName
        -int quantity
        -BigDecimal unitPrice
        -BigDecimal subtotal
    }
    
    %% Repositories
    class CartRepository {
        +save(CartEntity)
        +findById(String)
        +findByUserId(String)
        +deleteById(String)
    }
    
    class CartItemRepository {
        +saveAll(List~CartItemEntity~)
        +findByCartId(String)
        +deleteByCartId(String)
        +deleteByCartItemId(String)
    }
    
    %% Service Layer
    class CartService {
        <<interface>>
        +createCart(CreateCartCommand)
        +addItemToCart(AddItemToCartCommand)
        +removeItemFromCart(RemoveItemFromCartCommand)
        +getCart(String)
        +getCartByUserId(String)
        +deleteCart(String)
    }
    
    class CartServiceImpl {
        -CartRepository cartRepository
        -CartItemRepository cartItemRepository
        -CommandGateway commandGateway
        +createCart(CreateCartCommand)
        +addItemToCart(AddItemToCartCommand)
        +removeItemFromCart(RemoveItemFromCartCommand)
        +getCart(String)
        +getCartByUserId(String)
        +deleteCart(String)
    }
    
    %% Controller
    class CartController {
        -CartService cartService
        +createCart(CreateCartRequest)
        +addItemToCart(AddItemRequest)
        +removeItemFromCart(String, String)
        +getCart(String)
        +getCartByUserId(String)
        +deleteCart(String)
    }
    
    %% Event Handlers
    class CartEventHandler {
        -CartRepository cartRepository
        -CartItemRepository cartItemRepository
        +on(CartCreatedEvent)
        +on(ItemAddedToCartEvent)
        +on(ItemRemovedFromCartEvent)
    }
    
    %% Kafka Components
    class CartEventProducer {
        -KafkaTemplate kafkaTemplate
        +sendCartCreatedEvent(CartCreatedEvent)
        +sendItemAddedToCartEvent(ItemAddedToCartEvent)
        +sendItemRemovedFromCartEvent(ItemRemovedFromCartEvent)
    }
    
    class CartEventConsumer {
        -CartEventHandler eventHandler
        +handleCartCreatedEvent(CartCreatedEvent)
        +handleItemAddedToCartEvent(ItemAddedToCartEvent)
        +handleItemRemovedFromCartEvent(ItemRemovedFromCartEvent)
    }
    
    %% Relationships
    Cart "1" *-- "many" CartItem
    CartEntity "1" -- "many" CartItemEntity
    
    CartService <|.. CartServiceImpl
    CartServiceImpl --> CartRepository
    CartServiceImpl --> CartItemRepository
    
    CartController --> CartService
    
    CartEventHandler --> CartRepository
    CartEventHandler --> CartItemRepository
    
    CartEventConsumer --> CartEventHandler
    CartEventProducer --> Cart
    
    CartRepository --> CartEntity
    CartItemRepository --> CartItemEntity
```

This class diagram shows:

1. **Domain Model**: The `Cart` aggregate and `CartItem` value object
2. **Repository Layer**: `CartRepository` and `CartItemRepository` interfaces with their entity classes
3. **Service Layer**: `CartService` interface and `CartServiceImpl` implementation
4. **Controller Layer**: `CartController` for handling HTTP requests
5. **Event Handling**: `CartEventHandler` for processing events
6. **Kafka Integration**: `CartEventProducer` and `CartEventConsumer` for event-driven communication

#### Order Service Class Diagram

The following diagram illustrates the class structure and relationships within the Order Service, including the SAGA implementation:

```mermaid
classDiagram
    %% Domain Classes
    class Order {
        -String orderId
        -String userId
        -String cartId
        -List~OrderItem~ items
        -BigDecimal totalAmount
        -OrderStatus status
        -String shippingAddress
        +handle(CreateOrderCommand)
        +handle(UpdateOrderStatusCommand)
        +on(OrderCreatedEvent)
        +on(OrderStatusUpdatedEvent)
    }
    
    class OrderItem {
        -String orderItemId
        -String orderId
        -String productId
        -String productName
        -int quantity
        -BigDecimal unitPrice
        -BigDecimal subtotal
    }
    
    class OrderStatus {
        <<enumeration>>
        CREATED
        PAID
        PAYMENT_FAILED
        DISPATCHED
        DISPATCH_FAILED
        DELIVERED
        DELIVERY_FAILED
        ERROR
    }
    
    %% SAGA Orchestration
    class OrderSaga {
        -CommandGateway commandGateway
        +handle(OrderCreatedEvent)
        +handle(PaymentProcessedEvent)
        +handle(OrderDispatchedEvent)
        +handle(OrderDeliveredEvent)
    }
    
    %% Repository Entities
    class OrderEntity {
        -String orderId
        -String userId
        -String cartId
        -BigDecimal totalAmount
        -String status
        -String shippingAddress
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
    }
    
    class OrderItemEntity {
        -String orderItemId
        -String orderId
        -String productId
        -String productName
        -int quantity
        -BigDecimal unitPrice
        -BigDecimal subtotal
    }
    
    %% Repositories
    class OrderRepository {
        +save(OrderEntity)
        +findById(String)
        +findByUserId(String)
        +updateStatus(String, String)
    }
    
    class OrderItemRepository {
        +saveAll(List~OrderItemEntity~)
        +findByOrderId(String)
    }
    
    %% Service Layer
    class OrderService {
        <<interface>>
        +createOrder(CreateOrderCommand)
        +updateOrderStatus(UpdateOrderStatusCommand)
        +getOrder(String)
        +getOrdersByUserId(String)
    }
    
    class OrderServiceImpl {
        -OrderRepository orderRepository
        -OrderItemRepository orderItemRepository
        -CommandGateway commandGateway
        +createOrder(CreateOrderCommand)
        +updateOrderStatus(UpdateOrderStatusCommand)
        +getOrder(String)
        +getOrdersByUserId(String)
    }
    
    %% Controller
    class OrderController {
        -OrderService orderService
        +createOrder(CreateOrderRequest)
        +getOrder(String)
        +getOrdersByUserId(String)
    }
    
    %% Event Handlers
    class OrderEventHandler {
        -OrderRepository orderRepository
        -OrderItemRepository orderItemRepository
        +on(OrderCreatedEvent)
        +on(OrderStatusUpdatedEvent)
    }
    
    %% Kafka Components
    class OrderEventProducer {
        -KafkaTemplate kafkaTemplate
        +sendOrderCreatedEvent(OrderCreatedEvent)
        +sendOrderStatusUpdatedEvent(OrderStatusUpdatedEvent)
    }
    
    class OrderEventConsumer {
        -OrderEventHandler eventHandler
        -OrderSaga orderSaga
        +handleOrderCreatedEvent(OrderCreatedEvent)
        +handlePaymentProcessedEvent(PaymentProcessedEvent)
        +handleOrderDispatchedEvent(OrderDispatchedEvent)
        +handleOrderDeliveredEvent(OrderDeliveredEvent)
        +handleOrderStatusUpdatedEvent(OrderStatusUpdatedEvent)
    }
    
    %% Relationships
    Order "1" *-- "many" OrderItem
    Order "1" *-- "1" OrderStatus
    OrderEntity "1" -- "many" OrderItemEntity
    
    OrderService <|.. OrderServiceImpl
    OrderServiceImpl --> OrderRepository
    OrderServiceImpl --> OrderItemRepository
    
    OrderController --> OrderService
    
    OrderEventHandler --> OrderRepository
    OrderEventHandler --> OrderItemRepository
    
    OrderEventConsumer --> OrderEventHandler
    OrderEventConsumer --> OrderSaga
    OrderEventProducer --> Order
    
    OrderRepository --> OrderEntity
    OrderItemRepository --> OrderItemEntity
    
    OrderSaga --> OrderService
```

This class diagram shows:

1. **Domain Model**: The `Order` aggregate, `OrderItem` value object, and `OrderStatus` enumeration
2. **SAGA Orchestration**: The `OrderSaga` class that coordinates the order processing workflow
3. **Repository Layer**: `OrderRepository` and `OrderItemRepository` interfaces with their entity classes
4. **Service Layer**: `OrderService` interface and `OrderServiceImpl` implementation
5. **Controller Layer**: `OrderController` for handling HTTP requests
6. **Event Handling**: `OrderEventHandler` for processing events
7. **Kafka Integration**: `OrderEventProducer` and `OrderEventConsumer` for event-driven communication

#### Database Schema Diagrams

The following diagrams illustrate the database schemas for each service:

##### Cart Service Database Schema

```mermaid
erDiagram
    CARTS {
        string cart_id PK
        string user_id
        timestamp created_at
        timestamp updated_at
    }
    
    CART_ITEMS {
        string cart_item_id PK
        string cart_id FK
        string product_id
        string product_name
        int quantity
        decimal unit_price
        timestamp created_at
        timestamp updated_at
    }
    
    CARTS ||--o{ CART_ITEMS : contains
```

##### Order Service Database Schema

```mermaid
erDiagram
    ORDERS {
        string order_id PK
        string user_id
        string cart_id
        decimal total_amount
        string status
        string shipping_address
        timestamp created_at
        timestamp updated_at
    }
    
    ORDER_ITEMS {
        string order_item_id PK
        string order_id FK
        string product_id
        string product_name
        int quantity
        decimal unit_price
        decimal subtotal
    }
    
    ORDERS ||--o{ ORDER_ITEMS : contains
```

##### Payment Service Database Schema

```mermaid
erDiagram
    PAYMENTS {
        string payment_id PK
        string order_id
        decimal amount
        string payment_method
        string status
        timestamp created_at
        timestamp updated_at
    }
```

##### Dispatch Service Database Schema

```mermaid
erDiagram
    DISPATCHES {
        string dispatch_id PK
        string order_id
        string carrier
        string tracking_number
        string status
        timestamp dispatch_date
        timestamp created_at
        timestamp updated_at
    }
```

##### Delivery Service Database Schema

```mermaid
erDiagram
    DELIVERIES {
        string delivery_id PK
        string dispatch_id
        string order_id
        string tracking_number
        string delivery_address
        string status
        timestamp estimated_delivery_time
        timestamp actual_delivery_time
        timestamp created_at
        timestamp updated_at
    }
```

#### CQRS Implementation Details Diagram

The following diagram illustrates the detailed implementation of the CQRS pattern in the services:

```mermaid
graph TD
    %% Client Interaction
    Client[Client] --> Controller[REST Controller]
    
    %% Command Side
    Controller --> CommandAPI[Command API]
    CommandAPI --> CommandHandler[Command Handler]
    CommandHandler --> Aggregate[Aggregate Root]
    Aggregate --> EventSourcing[Event Sourcing]
    EventSourcing --> EventStore[(Event Store)]
    Aggregate --> EventPublisher[Event Publisher]
    EventPublisher --> KafkaBus[Kafka Event Bus]
    
    %% Query Side
    Controller --> QueryAPI[Query API]
    QueryAPI --> QueryHandler[Query Handler]
    QueryHandler --> ReadModel[Read Model]
    ReadModel --> Database[(Database)]
    KafkaBus --> EventHandler[Event Handler]
    EventHandler --> ReadModel
    
    %% Styling
    classDef client fill:#f9f,stroke:#333,stroke-width:2px
    classDef api fill:#bfb,stroke:#333,stroke-width:2px
    classDef handler fill:#bbf,stroke:#333,stroke-width:2px
    classDef domain fill:#fbb,stroke:#333,stroke-width:2px
    classDef infrastructure fill:#fbf,stroke:#333,stroke-width:2px
    classDef database fill:#ddd,stroke:#333,stroke-width:2px
    
    class Client client
    class Controller,CommandAPI,QueryAPI api
    class CommandHandler,QueryHandler,EventHandler handler
    class Aggregate,ReadModel domain
    class EventSourcing,EventPublisher,KafkaBus infrastructure
    class EventStore,Database database
```

This diagram shows:

1. **Command Side**: Handles commands that modify state
   - Command API receives commands from the controller
   - Command Handler processes commands
   - Aggregate Root applies business rules and generates events
   - Event Sourcing stores events in the Event Store
   - Event Publisher sends events to the Kafka Event Bus

2. **Query Side**: Handles queries that read state
   - Query API receives queries from the controller
   - Query Handler processes queries
   - Read Model provides optimized data for queries
   - Database stores the read model
   - Event Handler updates the read model based on events from Kafka

#### SAGA Implementation Details Diagram

The following diagram illustrates the detailed implementation of the SAGA pattern in the Order Service:

```mermaid
graph TD
    %% Order Creation
    OrderCreated[Order Created Event] --> OrderSaga[Order Saga]
    
    %% Payment Processing
    OrderSaga --> ProcessPayment[Process Payment Command]
    ProcessPayment --> PaymentService[Payment Service]
    PaymentService --> PaymentProcessed[Payment Processed Event]
    PaymentProcessed --> OrderSaga
    
    %% Payment Success Path
    OrderSaga --> UpdateOrderPaid[Update Order Status: PAID]
    UpdateOrderPaid --> OrderService[Order Service]
    
    %% Dispatch Processing
    OrderSaga --> DispatchOrder[Dispatch Order Command]
    DispatchOrder --> DispatchService[Dispatch Service]
    DispatchService --> OrderDispatched[Order Dispatched Event]
    OrderDispatched --> OrderSaga
    
    %% Dispatch Success Path
    OrderSaga --> UpdateOrderDispatched[Update Order Status: DISPATCHED]
    UpdateOrderDispatched --> OrderService
    
    %% Delivery Processing
    OrderSaga --> DeliverOrder[Deliver Order Command]
    DeliverOrder --> DeliveryService[Delivery Service]
    DeliveryService --> OrderDelivered[Order Delivered Event]
    OrderDelivered --> OrderSaga
    
    %% Delivery Success Path
    OrderSaga --> UpdateOrderDelivered[Update Order Status: DELIVERED]
    UpdateOrderDelivered --> OrderService
    
    %% Compensation Transactions
    PaymentProcessed -.-> |FAILED| PaymentFailed[Payment Failed]
    PaymentFailed -.-> UpdateOrderPaymentFailed[Update Order Status: PAYMENT_FAILED]
    UpdateOrderPaymentFailed -.-> OrderService
    
    OrderDispatched -.-> |FAILED| DispatchFailed[Dispatch Failed]
    DispatchFailed -.-> UpdateOrderDispatchFailed[Update Order Status: DISPATCH_FAILED]
    UpdateOrderDispatchFailed -.-> OrderService
    
    OrderDelivered -.-> |FAILED| DeliveryFailed[Delivery Failed]
    DeliveryFailed -.-> UpdateOrderDeliveryFailed[Update Order Status: DELIVERY_FAILED]
    UpdateOrderDeliveryFailed -.-> OrderService
    
    %% System Error Handling
    OrderSaga -.-> |Exception| SystemError[System Error]
    SystemError -.-> UpdateOrderError[Update Order Status: ERROR]
    UpdateOrderError -.-> OrderService
    
    %% Styling
    classDef event fill:#f9f,stroke:#333,stroke-width:2px
    classDef command fill:#bfb,stroke:#333,stroke-width:2px
    classDef service fill:#bbf,stroke:#333,stroke-width:2px
    classDef saga fill:#fbb,stroke:#333,stroke-width:2px
    classDef compensation fill:#fbf,stroke:#333,stroke-width:2px
    
    class OrderCreated,PaymentProcessed,OrderDispatched,OrderDelivered event
    class ProcessPayment,UpdateOrderPaid,DispatchOrder,UpdateOrderDispatched,DeliverOrder,UpdateOrderDelivered command
    class PaymentService,OrderService,DispatchService,DeliveryService service
    class OrderSaga saga
    class PaymentFailed,DispatchFailed,DeliveryFailed,SystemError,UpdateOrderPaymentFailed,UpdateOrderDispatchFailed,UpdateOrderDeliveryFailed,UpdateOrderError compensation
```

This diagram shows:

1. **SAGA Orchestration**: The Order Saga coordinates the entire workflow
2. **Sequential Steps**: Order creation → Payment → Dispatch → Delivery
3. **Command-Event Flow**: Commands trigger service operations, which produce events
4. **Compensation Transactions**: Handling failures at each step
   - Payment failure: Update order status to PAYMENT_FAILED
   - Dispatch failure: Update order status to DISPATCH_FAILED
   - Delivery failure: Update order status to DELIVERY_FAILED
5. **System Error Handling**: Handling exceptions during saga execution

### Conclusion

The Amazon Platform project demonstrates a modern microservices architecture using SAGA and CQRS patterns with event-driven communication through Kafka. The reactive programming model with R2DBC provides scalability and performance benefits. The project serves as a reference implementation for building complex e-commerce systems with distributed transaction management.

The low-level diagrams provide detailed insights into the implementation of each service, showing the class structures, relationships, database schemas, and pattern implementations. These diagrams complement the high-level architecture diagrams and sequence diagrams to provide a comprehensive understanding of the system.