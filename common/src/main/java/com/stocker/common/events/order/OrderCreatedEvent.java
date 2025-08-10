package com.stocker.common.events.order;

import com.stocker.common.models.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private String orderId;
    private String userId;
    private String cartId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private LocalDateTime createdAt;
}