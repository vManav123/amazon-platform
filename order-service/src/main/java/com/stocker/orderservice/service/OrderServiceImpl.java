package com.stocker.orderservice.service;

import com.stocker.common.commands.order.CreateOrderCommand;
import com.stocker.common.commands.order.UpdateOrderStatusCommand;
import com.stocker.common.models.OrderItem;
import com.stocker.orderservice.domain.OrderStatus;
import com.stocker.orderservice.repository.OrderItemRepository;
import com.stocker.orderservice.repository.OrderRepository;
import com.stocker.orderservice.repository.entity.OrderEntity;
import com.stocker.orderservice.repository.entity.OrderItemEntity;
import com.stocker.orderservice.service.dto.OrderDTO;
import com.stocker.orderservice.service.dto.OrderItemDTO;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final WebClient.Builder webClientBuilder;
    
    @Value("${cart.service.url:http://localhost:8081}")
    private String cartServiceUrl;

    @Override
    public Mono<OrderDTO> createOrder(String userId, String cartId, String shippingAddress) {
        String orderId = UUID.randomUUID().toString();
        
        // Query the cart service to get cart items
        return getCartItems(cartId)
                .collectList()
                .flatMap(cartItems -> {
                    List<OrderItem> orderItems = cartItems.stream()
                            .map(item -> new OrderItem(
                                    UUID.randomUUID().toString(),
                                    item.getProductId(),
                                    item.getProductName(),
                                    item.getQuantity(),
                                    item.getUnitPrice()
                            ))
                            .collect(Collectors.toList());
                    
                    BigDecimal totalAmount = orderItems.stream()
                            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    CreateOrderCommand command = CreateOrderCommand.builder()
                            .orderId(orderId)
                            .userId(userId)
                            .cartId(cartId)
                            .items(orderItems)
                            .totalAmount(totalAmount)
                            .shippingAddress(shippingAddress)
                            .build();
                    
                    return Mono.fromFuture(commandGateway.send(command))
                            .then(saveOrder(command, OrderStatus.CREATED))
                            .flatMap(orderEntity -> saveOrderItems(orderEntity.getId(), orderItems)
                                    .collectList()
                                    .map(orderItemEntities -> mapToOrderDTO(orderEntity, orderItemEntities)));
                });
    }

    @Override
    public Mono<OrderDTO> getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .flatMap(orderEntity -> orderItemRepository.findByOrderId(orderId)
                        .collectList()
                        .map(orderItemEntities -> mapToOrderDTO(orderEntity, orderItemEntities)));
    }

    @Override
    public Flux<OrderDTO> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId)
                .flatMap(orderEntity -> orderItemRepository.findByOrderId(orderEntity.getId())
                        .collectList()
                        .map(orderItemEntities -> mapToOrderDTO(orderEntity, orderItemEntities)));
    }

    @Override
    public Mono<OrderDTO> updateOrderStatus(String orderId, OrderStatus status) {
        return orderRepository.findById(orderId)
                .flatMap(orderEntity -> {
                    UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(
                            orderId,
                            status.toString()
                    );
                    
                    return Mono.fromFuture(commandGateway.send(command))
                            .then(Mono.defer(() -> {
                                orderEntity.setStatus(status.toString());
                                orderEntity.setUpdatedAt(Instant.now());
                                return orderRepository.save(orderEntity);
                            }));
                })
                .flatMap(orderEntity -> orderItemRepository.findByOrderId(orderId)
                        .collectList()
                        .map(orderItemEntities -> mapToOrderDTO(orderEntity, orderItemEntities)));
    }

    @Override
    public Mono<Void> cancelOrder(String orderId) {
        return updateOrderStatus(orderId, OrderStatus.CANCELLED)
                .then();
    }
    
    private Mono<OrderEntity> saveOrder(CreateOrderCommand command, OrderStatus status) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(command.getOrderId())
                .userId(command.getUserId())
                .cartId(command.getCartId())
                .totalAmount(command.getTotalAmount())
                .shippingAddress(command.getShippingAddress())
                .status(status.toString())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        return orderRepository.save(orderEntity);
    }
    
    private Flux<OrderItemEntity> saveOrderItems(String orderId, List<OrderItem> items) {
        List<OrderItemEntity> orderItemEntities = items.stream()
                .map(item -> {
                    BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    
                    return OrderItemEntity.builder()
                            .id(UUID.randomUUID().toString())
                            .orderId(orderId)
                            .productId(item.getProductId())
                            .productName(item.getProductName())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .subtotal(subtotal)
                            .build();
                })
                .collect(Collectors.toList());
        
        return orderItemRepository.saveAll(orderItemEntities);
    }
    
    private OrderDTO mapToOrderDTO(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        List<OrderItemDTO> orderItemDTOs = orderItemEntities.stream()
                .map(this::mapToOrderItemDTO)
                .collect(Collectors.toList());
        
        return OrderDTO.builder()
                .id(orderEntity.getId())
                .userId(orderEntity.getUserId())
                .cartId(orderEntity.getCartId())
                .items(orderItemDTOs)
                .totalAmount(orderEntity.getTotalAmount())
                .shippingAddress(orderEntity.getShippingAddress())
                .status(OrderStatus.valueOf(orderEntity.getStatus()))
                .createdAt(orderEntity.getCreatedAt())
                .updatedAt(orderEntity.getUpdatedAt())
                .build();
    }
    
    private OrderItemDTO mapToOrderItemDTO(OrderItemEntity orderItemEntity) {
        return OrderItemDTO.builder()
                .id(orderItemEntity.getId())
                .productId(orderItemEntity.getProductId())
                .productName(orderItemEntity.getProductName())
                .quantity(orderItemEntity.getQuantity())
                .unitPrice(orderItemEntity.getUnitPrice())
                .subtotal(orderItemEntity.getSubtotal())
                .build();
    }
    
    private Flux<CartItemDTO> getCartItems(String cartId) {
        return webClientBuilder.build()
                .get()
                .uri(cartServiceUrl + "/api/carts/" + cartId)
                .retrieve()
                .bodyToMono(CartDTO.class)
                .flatMapMany(cartDTO -> Flux.fromIterable(cartDTO.getItems()));
    }
    
    // DTOs for Cart Service
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class CartDTO {
        private String cartId;
        private String userId;
        private List<CartItemDTO> items;
        private BigDecimal totalAmount;
    }
    
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    private static class CartItemDTO {
        private String cartItemId;
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal unitPrice;
    }
}