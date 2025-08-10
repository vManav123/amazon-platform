package com.stocker.common.commands.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddItemToCartCommand {
    @TargetAggregateIdentifier
    private String cartId;
    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
}