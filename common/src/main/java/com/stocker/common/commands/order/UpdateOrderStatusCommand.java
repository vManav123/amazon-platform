package com.stocker.common.commands.order;

import com.stocker.common.models.Order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusCommand {
    @TargetAggregateIdentifier
    private String orderId;
    private OrderStatus status;
    private String reason;
}