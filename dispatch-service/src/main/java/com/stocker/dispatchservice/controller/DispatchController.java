package com.stocker.dispatchservice.controller;

import com.stocker.dispatchservice.service.DispatchService;
import com.stocker.dispatchservice.service.dto.DispatchDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/dispatches")
@RequiredArgsConstructor
@Slf4j
public class DispatchController {

    private final DispatchService dispatchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DispatchDTO> dispatchOrder(@RequestParam String orderId,
                                          @RequestParam String carrier) {
        log.info("Dispatching order: {}", orderId);
        return dispatchService.dispatchOrder(orderId, carrier);
    }

    @GetMapping("/{dispatchId}")
    public Mono<DispatchDTO> getDispatch(@PathVariable String dispatchId) {
        log.info("Getting dispatch: {}", dispatchId);
        return dispatchService.getDispatch(dispatchId);
    }

    @GetMapping("/order/{orderId}")
    public Mono<DispatchDTO> getDispatchByOrderId(@PathVariable String orderId) {
        log.info("Getting dispatch for order: {}", orderId);
        return dispatchService.getDispatchByOrderId(orderId);
    }

    @GetMapping("/tracking/{trackingNumber}")
    public Mono<DispatchDTO> getDispatchByTrackingNumber(@PathVariable String trackingNumber) {
        log.info("Getting dispatch by tracking number: {}", trackingNumber);
        return dispatchService.getDispatchByTrackingNumber(trackingNumber);
    }

    @GetMapping("/carrier/{carrier}")
    public Flux<DispatchDTO> getDispatchesByCarrier(@PathVariable String carrier) {
        log.info("Getting dispatches for carrier: {}", carrier);
        return dispatchService.getDispatchesByCarrier(carrier);
    }
}