package com.stocker.common.events.order;

import com.stocker.common.models.Order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdatedEvent {
    private String orderId;
    private OrderStatus previousStatus;
    private OrderStatus newStatus;
    private String reason;
    private LocalDateTime updatedAt;
}