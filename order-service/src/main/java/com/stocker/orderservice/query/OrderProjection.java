package com.stocker.orderservice.query;

import com.stocker.common.events.order.OrderCreatedEvent;
import com.stocker.common.events.order.OrderStatusUpdatedEvent;
import com.stocker.orderservice.domain.OrderStatus;
import com.stocker.orderservice.repository.OrderItemRepository;
import com.stocker.orderservice.repository.OrderRepository;
import com.stocker.orderservice.repository.entity.OrderEntity;
import com.stocker.orderservice.repository.entity.OrderItemEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProjection {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @EventHandler
    public void on(OrderCreatedEvent event) {
        log.info("Handling OrderCreatedEvent: {}", event.getOrderId());
        
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(event.getOrderId());
        orderEntity.setUserId(event.getUserId());
        orderEntity.setCartId(event.getCartId());
        orderEntity.setTotalAmount(event.getTotalAmount());
        orderEntity.setShippingAddress(event.getShippingAddress());
        orderEntity.setStatus(OrderStatus.valueOf(event.getStatus()));
        orderEntity.setCreatedAt(event.getCreatedAt());
        orderEntity.setUpdatedAt(event.getCreatedAt());
        
        orderRepository.save(orderEntity).subscribe(
            savedOrder -> {
                log.info("Order saved: {}", savedOrder.getOrderId());
                
                // Save order items
                event.getItems().forEach(item -> {
                    OrderItemEntity orderItemEntity = new OrderItemEntity();
                    orderItemEntity.setOrderItemId(item.getOrderItemId());
                    orderItemEntity.setOrderId(event.getOrderId());
                    orderItemEntity.setProductId(item.getProductId());
                    orderItemEntity.setProductName(item.getProductName());
                    orderItemEntity.setQuantity(item.getQuantity());
                    orderItemEntity.setUnitPrice(item.getUnitPrice());
                    
                    orderItemRepository.save(orderItemEntity).subscribe(
                        savedItem -> log.info("Order item saved: {}", savedItem.getOrderItemId()),
                        error -> log.error("Error saving order item: {}", error.getMessage())
                    );
                });
            },
            error -> log.error("Error saving order: {}", error.getMessage())
        );
    }

    @EventHandler
    public void on(OrderStatusUpdatedEvent event) {
        log.info("Handling OrderStatusUpdatedEvent: {}", event.getOrderId());
        
        orderRepository.findById(event.getOrderId())
            .flatMap(orderEntity -> {
                orderEntity.setStatus(OrderStatus.valueOf(event.getStatus()));
                orderEntity.setUpdatedAt(event.getUpdatedAt());
                return orderRepository.save(orderEntity);
            })
            .subscribe(
                updatedOrder -> log.info("Order status updated: {}", updatedOrder.getOrderId()),
                error -> log.error("Error updating order status: {}", error.getMessage())
            );
    }

    @QueryHandler
    public Mono<OrderEntity> findOrder(FindOrderQuery query) {
        log.info("Handling FindOrderQuery: {}", query.getOrderId());
        return orderRepository.findById(query.getOrderId());
    }

    @QueryHandler
    public Flux<OrderEntity> findOrdersByUserId(FindOrdersByUserIdQuery query) {
        log.info("Handling FindOrdersByUserIdQuery: {}", query.getUserId());
        return orderRepository.findByUserId(query.getUserId());
    }

    @QueryHandler
    public Flux<OrderItemEntity> findOrderItems(FindOrderItemsQuery query) {
        log.info("Handling FindOrderItemsQuery: {}", query.getOrderId());
        return orderItemRepository.findByOrderId(query.getOrderId());
    }

    @QueryHandler
    public Flux<OrderEntity> findOrdersByStatus(FindOrdersByStatusQuery query) {
        log.info("Handling FindOrdersByStatusQuery: {}", query.getStatus());
        return orderRepository.findByStatus(OrderStatus.valueOf(query.getStatus()));
    }
}