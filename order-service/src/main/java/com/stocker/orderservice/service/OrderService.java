package com.stocker.orderservice.service;

import com.stocker.orderservice.domain.OrderStatus;
import com.stocker.orderservice.service.dto.OrderDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {
    
    Mono<OrderDTO> createOrder(String userId, String cartId, String shippingAddress);
    
    Mono<OrderDTO> getOrder(String orderId);
    
    Flux<OrderDTO> getOrdersByUserId(String userId);
    
    Mono<OrderDTO> updateOrderStatus(String orderId, OrderStatus status);
    
    Mono<Void> cancelOrder(String orderId);
}