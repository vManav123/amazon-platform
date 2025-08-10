package com.stocker.common.commands.order;

import com.stocker.common.models.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    private String orderId;
    private String userId;
    private String cartId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String shippingAddress;
}