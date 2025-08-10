package com.stocker.cartservice.service;

import com.stocker.cartservice.domain.CartItem;
import com.stocker.cartservice.kafka.CartEventProducer;
import com.stocker.cartservice.repository.CartItemRepository;
import com.stocker.cartservice.repository.CartRepository;
import com.stocker.cartservice.repository.entity.CartEntity;
import com.stocker.cartservice.repository.entity.CartItemEntity;
import com.stocker.cartservice.service.dto.CartDTO;
import com.stocker.cartservice.service.dto.CartItemDTO;
import com.stocker.common.commands.cart.AddItemToCartCommand;
import com.stocker.common.commands.cart.CreateCartCommand;
import com.stocker.common.commands.cart.RemoveItemFromCartCommand;
import com.stocker.common.events.cart.CartCreatedEvent;
import com.stocker.common.events.cart.ItemAddedToCartEvent;
import com.stocker.common.events.cart.ItemRemovedFromCartEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final CartEventProducer cartEventProducer;

    @Override
    public Mono<CartDTO> createCart(String userId) {
        String cartId = UUID.randomUUID().toString();
        
        return Mono.fromFuture(commandGateway.send(new CreateCartCommand(cartId, userId)))
                .then(Mono.just(CartDTO.builder()
                        .cartId(cartId)
                        .userId(userId)
                        .items(List.of())
                        .totalAmount(BigDecimal.ZERO)
                        .build()))
                .flatMap(cartDTO -> {
                    // Publish CartCreatedEvent to Kafka
                    CartCreatedEvent event = CartCreatedEvent.builder()
                            .cartId(cartDTO.getCartId())
                            .userId(cartDTO.getUserId())
                            .timestamp(System.currentTimeMillis())
                            .build();
                    
                    return cartEventProducer.sendCartCreatedEvent(event)
                            .thenReturn(cartDTO);
                });
    }

    @Override
    public Mono<CartDTO> getCart(String cartId) {
        return cartRepository.findById(cartId)
                .flatMap(this::mapCartEntityToDTO);
    }

    @Override
    public Mono<CartDTO> getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId)
                .flatMap(this::mapCartEntityToDTO);
    }

    @Override
    public Mono<CartDTO> addItemToCart(String cartId, CartItemDTO cartItemDTO) {
        return Mono.fromFuture(commandGateway.send(new AddItemToCartCommand(
                cartId,
                cartItemDTO.getProductId(),
                cartItemDTO.getProductName(),
                cartItemDTO.getQuantity(),
                cartItemDTO.getUnitPrice()
        )))
        .then(getCart(cartId))
        .flatMap(cartDTO -> {
            // Publish ItemAddedToCartEvent to Kafka
            ItemAddedToCartEvent event = ItemAddedToCartEvent.builder()
                    .cartId(cartId)
                    .productId(cartItemDTO.getProductId())
                    .productName(cartItemDTO.getProductName())
                    .quantity(cartItemDTO.getQuantity())
                    .unitPrice(cartItemDTO.getUnitPrice())
                    .timestamp(System.currentTimeMillis())
                    .build();
            
            return cartEventProducer.sendItemAddedToCartEvent(event)
                    .thenReturn(cartDTO);
        });
    }

    @Override
    public Mono<CartDTO> removeItemFromCart(String cartId, String cartItemId) {
        // First get the cart item details before removing it
        return cartItemRepository.findById(cartItemId)
                .flatMap(cartItemEntity -> {
                    // Then remove the item
                    return Mono.fromFuture(commandGateway.send(new RemoveItemFromCartCommand(
                            cartId,
                            cartItemId
                    )))
                    .then(getCart(cartId))
                    .flatMap(cartDTO -> {
                        // Publish ItemRemovedFromCartEvent to Kafka
                        ItemRemovedFromCartEvent event = ItemRemovedFromCartEvent.builder()
                                .cartId(cartId)
                                .cartItemId(cartItemId)
                                .productId(cartItemEntity.getProductId())
                                .timestamp(System.currentTimeMillis())
                                .build();
                        
                        return cartEventProducer.sendItemRemovedFromCartEvent(event)
                                .thenReturn(cartDTO);
                    });
                });
    }

    @Override
    public Mono<CartDTO> updateCartItemQuantity(String cartId, String cartItemId, int quantity) {
        return cartItemRepository.findById(cartItemId)
                .flatMap(cartItemEntity -> {
                    // First remove the item
                    return removeItemFromCart(cartId, cartItemId)
                            // Then add it back with the new quantity
                            .flatMap(cart -> {
                                CartItemDTO updatedItem = CartItemDTO.builder()
                                        .productId(cartItemEntity.getProductId())
                                        .productName(cartItemEntity.getProductName())
                                        .quantity(quantity)
                                        .unitPrice(cartItemEntity.getUnitPrice())
                                        .build();
                                return addItemToCart(cartId, updatedItem);
                            });
                });
    }

    @Override
    public Mono<Void> clearCart(String cartId) {
        return cartItemRepository.deleteByCartId(cartId);
    }

    private Mono<CartDTO> mapCartEntityToDTO(CartEntity cartEntity) {
        return cartItemRepository.findByCartId(cartEntity.getCartId())
                .collectList()
                .map(cartItemEntities -> {
                    List<CartItemDTO> cartItemDTOs = cartItemEntities.stream()
                            .map(this::mapCartItemEntityToDTO)
                            .collect(Collectors.toList());
                    
                    BigDecimal totalAmount = cartItemDTOs.stream()
                            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    return CartDTO.builder()
                            .cartId(cartEntity.getCartId())
                            .userId(cartEntity.getUserId())
                            .items(cartItemDTOs)
                            .totalAmount(totalAmount)
                            .build();
                });
    }

    private CartItemDTO mapCartItemEntityToDTO(CartItemEntity entity) {
        return CartItemDTO.builder()
                .cartItemId(entity.getCartItemId())
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .build();
    }
}