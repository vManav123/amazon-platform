package com.stocker.cartservice.service;

import com.stocker.cartservice.service.dto.CartDTO;
import com.stocker.cartservice.service.dto.CartItemDTO;
import reactor.core.publisher.Mono;

public interface CartService {
    Mono<CartDTO> createCart(String userId);
    Mono<CartDTO> getCart(String cartId);
    Mono<CartDTO> getCartByUserId(String userId);
    Mono<CartDTO> addItemToCart(String cartId, CartItemDTO cartItemDTO);
    Mono<CartDTO> removeItemFromCart(String cartId, String cartItemId);
    Mono<CartDTO> updateCartItemQuantity(String cartId, String cartItemId, int quantity);
    Mono<Void> clearCart(String cartId);
}