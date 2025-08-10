package com.stocker.deliveryservice.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDTO {
    private String deliveryId;
    private String dispatchId;
    private String orderId;
    private String trackingNumber;
    private String deliveryAddress;
    private String status;
    private LocalDateTime deliveredAt;
    private String receiverName;
    private String deliveryNotes;
}