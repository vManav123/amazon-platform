package com.stocker.common.events.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveredEvent {
    private String deliveryId;
    private String dispatchId;
    private String orderId;
    private String trackingNumber;
    private String deliveryAddress;
    private String receiverName;
    private String deliveryNotes;
    private LocalDateTime deliveredAt;
}