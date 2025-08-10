package com.stocker.cartservice.repository;

import com.stocker.cartservice.repository.entity.CartItemEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CartItemRepository extends R2dbcRepository<CartItemEntity, String> {
    Flux<CartItemEntity> findByCartId(String cartId);
    Mono<CartItemEntity> findByCartIdAndProductId(String cartId, String productId);
    Mono<Void> deleteByCartId(String cartId);
}