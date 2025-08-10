package com.stocker.common.events.dispatch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDispatchedEvent {
    private String dispatchId;
    private String orderId;
    private String shippingAddress;
    private String trackingNumber;
    private String carrier;
    private LocalDateTime dispatchedAt;
    private LocalDateTime estimatedDeliveryDate;
}