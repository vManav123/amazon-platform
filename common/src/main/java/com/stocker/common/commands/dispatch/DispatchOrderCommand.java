package com.stocker.common.commands.dispatch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchOrderCommand {
    @TargetAggregateIdentifier
    private String dispatchId;
    private String orderId;
    private String shippingAddress;
    private String trackingNumber;
}