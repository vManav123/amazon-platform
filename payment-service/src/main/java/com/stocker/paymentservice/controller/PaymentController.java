package com.stocker.paymentservice.controller;

import com.stocker.paymentservice.service.PaymentService;
import com.stocker.paymentservice.service.dto.PaymentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PaymentDTO> processPayment(@RequestParam String orderId,
                                          @RequestParam BigDecimal amount,
                                          @RequestParam String paymentMethod) {
        return paymentService.processPayment(orderId, amount, paymentMethod);
    }

    @GetMapping("/{paymentId}")
    public Mono<PaymentDTO> getPayment(@PathVariable String paymentId) {
        return paymentService.getPayment(paymentId);
    }

    @GetMapping("/order/{orderId}")
    public Flux<PaymentDTO> getPaymentsByOrderId(@PathVariable String orderId) {
        return paymentService.getPaymentsByOrderId(orderId);
    }

    @PostMapping("/{paymentId}/refund")
    public Mono<PaymentDTO> refundPayment(@PathVariable String paymentId) {
        return paymentService.refundPayment(paymentId);
    }
}