package com.stocker.deliveryservice.controller;

import com.stocker.deliveryservice.service.DeliveryService;
import com.stocker.deliveryservice.service.dto.DeliveryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/{deliveryId}")
    public Mono<DeliveryDTO> getDelivery(@PathVariable String deliveryId) {
        log.info("Getting delivery: {}", deliveryId);
        return deliveryService.getDelivery(deliveryId);
    }

    @GetMapping("/order/{orderId}")
    public Mono<DeliveryDTO> getDeliveryByOrderId(@PathVariable String orderId) {
        log.info("Getting delivery by order ID: {}", orderId);
        return deliveryService.getDeliveryByOrderId(orderId);
    }

    @GetMapping("/tracking/{trackingNumber}")
    public Mono<DeliveryDTO> getDeliveryByTrackingNumber(@PathVariable String trackingNumber) {
        log.info("Getting delivery by tracking number: {}", trackingNumber);
        return deliveryService.getDeliveryByTrackingNumber(trackingNumber);
    }

    @GetMapping
    public Flux<DeliveryDTO> getDeliveriesByStatus(@RequestParam String status) {
        log.info("Getting deliveries by status: {}", status);
        return deliveryService.getDeliveriesByStatus(status);
    }
}