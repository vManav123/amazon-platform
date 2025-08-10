package com.stocker.cartservice.domain;

import com.stocker.common.commands.cart.AddItemToCartCommand;
import com.stocker.common.commands.cart.CreateCartCommand;
import com.stocker.common.commands.cart.RemoveItemFromCartCommand;
import com.stocker.common.events.cart.CartCreatedEvent;
import com.stocker.common.events.cart.ItemAddedToCartEvent;
import com.stocker.common.events.cart.ItemRemovedFromCartEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aggregate
@NoArgsConstructor
public class Cart {

    @AggregateIdentifier
    private String cartId;
    private String userId;
    private Map<String, CartItem> items = new HashMap<>();

    @CommandHandler
    public Cart(CreateCartCommand command) {
        AggregateLifecycle.apply(new CartCreatedEvent(
                command.getCartId(),
                command.getUserId()
        ));
    }

    @EventSourcingHandler
    public void on(CartCreatedEvent event) {
        this.cartId = event.getCartId();
        this.userId = event.getUserId();
        this.items = new HashMap<>();
    }

    @CommandHandler
    public void handle(AddItemToCartCommand command) {
        String cartItemId = UUID.randomUUID().toString();
        BigDecimal subtotal = command.getUnitPrice().multiply(BigDecimal.valueOf(command.getQuantity()));
        
        AggregateLifecycle.apply(new ItemAddedToCartEvent(
                command.getCartId(),
                cartItemId,
                command.getProductId(),
                command.getProductName(),
                command.getQuantity(),
                command.getUnitPrice(),
                subtotal
        ));
    }

    @EventSourcingHandler
    public void on(ItemAddedToCartEvent event) {
        CartItem item = new CartItem(
                event.getCartItemId(),
                event.getProductId(),
                event.getProductName(),
                event.getQuantity(),
                event.getUnitPrice()
        );
        
        this.items.put(event.getCartItemId(), item);
    }

    @CommandHandler
    public void handle(RemoveItemFromCartCommand command) {
        if (!items.containsKey(command.getCartItemId())) {
            throw new IllegalArgumentException("Item not found in cart: " + command.getCartItemId());
        }
        
        CartItem item = items.get(command.getCartItemId());
        
        AggregateLifecycle.apply(new ItemRemovedFromCartEvent(
                command.getCartId(),
                command.getCartItemId(),
                item.getProductId()
        ));
    }

    @EventSourcingHandler
    public void on(ItemRemovedFromCartEvent event) {
        this.items.remove(event.getCartItemId());
    }
}