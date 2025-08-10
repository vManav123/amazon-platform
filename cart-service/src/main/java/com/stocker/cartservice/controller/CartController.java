package com.stocker.cartservice.controller;

import com.stocker.cartservice.service.CartService;
import com.stocker.cartservice.service.dto.CartDTO;
import com.stocker.cartservice.service.dto.CartItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CartDTO> createCart(@RequestParam String userId) {
        log.info("Creating cart for user: {}", userId);
        return cartService.createCart(userId);
    }

    @GetMapping("/{cartId}")
    public Mono<CartDTO> getCart(@PathVariable String cartId) {
        log.info("Getting cart: {}", cartId);
        return cartService.getCart(cartId);
    }

    @GetMapping("/user/{userId}")
    public Mono<CartDTO> getCartByUserId(@PathVariable String userId) {
        log.info("Getting cart for user: {}", userId);
        return cartService.getCartByUserId(userId);
    }

    @PostMapping("/{cartId}/items")
    public Mono<CartDTO> addItemToCart(@PathVariable String cartId, @RequestBody CartItemDTO cartItemDTO) {
        log.info("Adding item to cart: {}", cartId);
        return cartService.addItemToCart(cartId, cartItemDTO);
    }

    @DeleteMapping("/{cartId}/items/{cartItemId}")
    public Mono<CartDTO> removeItemFromCart(@PathVariable String cartId, @PathVariable String cartItemId) {
        log.info("Removing item from cart: {}", cartId);
        return cartService.removeItemFromCart(cartId, cartItemId);
    }

    @PutMapping("/{cartId}/items/{cartItemId}")
    public Mono<CartDTO> updateCartItemQuantity(
            @PathVariable String cartId,
            @PathVariable String cartItemId,
            @RequestParam int quantity) {
        log.info("Updating item quantity in cart: {}", cartId);
        return cartService.updateCartItemQuantity(cartId, cartItemId, quantity);
    }

    @DeleteMapping("/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> clearCart(@PathVariable String cartId) {
        log.info("Clearing cart: {}", cartId);
        return cartService.clearCart(cartId);
    }
}