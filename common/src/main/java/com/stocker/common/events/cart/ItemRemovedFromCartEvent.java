package com.stocker.common.events.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRemovedFromCartEvent {
    private String cartId;
    private String cartItemId;
    private String productId;
}