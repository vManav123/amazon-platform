package com.stocker.cartservice.query;

import com.stocker.cartservice.repository.CartItemRepository;
import com.stocker.cartservice.repository.CartRepository;
import com.stocker.cartservice.repository.entity.CartEntity;
import com.stocker.cartservice.repository.entity.CartItemEntity;
import com.stocker.common.events.cart.CartCreatedEvent;
import com.stocker.common.events.cart.ItemAddedToCartEvent;
import com.stocker.common.events.cart.ItemRemovedFromCartEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartProjection {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @EventHandler
    public void on(CartCreatedEvent event) {
        log.info("Handling CartCreatedEvent: {}", event.getCartId());
        CartEntity cartEntity = new CartEntity();
        cartEntity.setCartId(event.getCartId());
        cartEntity.setUserId(event.getUserId());
        
        cartRepository.save(cartEntity).subscribe(
            savedCart -> log.info("Cart saved: {}", savedCart.getCartId()),
            error -> log.error("Error saving cart: {}", error.getMessage())
        );
    }

    @EventHandler
    public void on(ItemAddedToCartEvent event) {
        log.info("Handling ItemAddedToCartEvent: {}", event.getCartItemId());
        CartItemEntity cartItemEntity = new CartItemEntity();
        cartItemEntity.setCartItemId(event.getCartItemId());
        cartItemEntity.setCartId(event.getCartId());
        cartItemEntity.setProductId(event.getProductId());
        cartItemEntity.setProductName(event.getProductName());
        cartItemEntity.setQuantity(event.getQuantity());
        cartItemEntity.setUnitPrice(event.getUnitPrice());
        
        cartItemRepository.save(cartItemEntity).subscribe(
            savedItem -> log.info("Cart item saved: {}", savedItem.getCartItemId()),
            error -> log.error("Error saving cart item: {}", error.getMessage())
        );
    }

    @EventHandler
    public void on(ItemRemovedFromCartEvent event) {
        log.info("Handling ItemRemovedFromCartEvent: {}", event.getCartItemId());
        cartItemRepository.deleteById(event.getCartItemId()).subscribe(
            () -> log.info("Cart item deleted: {}", event.getCartItemId()),
            error -> log.error("Error deleting cart item: {}", error.getMessage())
        );
    }

    @QueryHandler
    public Mono<CartEntity> findCart(FindCartQuery query) {
        log.info("Handling FindCartQuery: {}", query.getCartId());
        return cartRepository.findById(query.getCartId());
    }

    @QueryHandler
    public Mono<CartEntity> findCartByUserId(FindCartByUserIdQuery query) {
        log.info("Handling FindCartByUserIdQuery: {}", query.getUserId());
        return cartRepository.findByUserId(query.getUserId());
    }

    @QueryHandler
    public Flux<CartItemEntity> findCartItems(FindCartItemsQuery query) {
        log.info("Handling FindCartItemsQuery: {}", query.getCartId());
        return cartItemRepository.findByCartId(query.getCartId());
    }

    @QueryHandler
    public Mono<BigDecimal> calculateCartTotal(CalculateCartTotalQuery query) {
        log.info("Handling CalculateCartTotalQuery: {}", query.getCartId());
        return cartItemRepository.findByCartId(query.getCartId())
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}