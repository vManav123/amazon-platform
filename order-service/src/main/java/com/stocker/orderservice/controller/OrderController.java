package com.stocker.orderservice.controller;

import com.stocker.orderservice.domain.OrderStatus;
import com.stocker.orderservice.service.OrderService;
import com.stocker.orderservice.service.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderDTO> createOrder(@RequestParam String userId,
                                      @RequestParam String cartId,
                                      @RequestParam String shippingAddress) {
        return orderService.createOrder(userId, cartId, shippingAddress);
    }

    @GetMapping("/{orderId}")
    public Mono<OrderDTO> getOrder(@PathVariable String orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping("/user/{userId}")
    public Flux<OrderDTO> getOrdersByUserId(@PathVariable String userId) {
        return orderService.getOrdersByUserId(userId);
    }

    @PutMapping("/{orderId}/status")
    public Mono<OrderDTO> updateOrderStatus(@PathVariable String orderId,
                                           @RequestParam OrderStatus status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> cancelOrder(@PathVariable String orderId) {
        return orderService.cancelOrder(orderId);
    }
}