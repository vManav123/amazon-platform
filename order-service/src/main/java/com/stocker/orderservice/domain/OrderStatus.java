package com.stocker.orderservice.domain;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAID,                  // New status for successful payment
    PAYMENT_COMPLETED,     // Keeping for backward compatibility
    PAYMENT_FAILED,
    PROCESSING,
    DISPATCHED,
    DISPATCH_FAILED,       // New status for dispatch failure
    DELIVERY_FAILED,       // New status for delivery failure
    DELIVERED,
    CANCELLED,
    ERROR                  // General error status
}