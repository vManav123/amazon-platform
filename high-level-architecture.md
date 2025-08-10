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