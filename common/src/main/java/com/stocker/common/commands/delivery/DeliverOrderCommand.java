package com.stocker.common.commands.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliverOrderCommand {
    @TargetAggregateIdentifier
    private String deliveryId;
    private String dispatchId;
    private String orderId;
    private String trackingNumber;
    private String deliveryAddress;
    private LocalDateTime estimatedDeliveryTime;
}