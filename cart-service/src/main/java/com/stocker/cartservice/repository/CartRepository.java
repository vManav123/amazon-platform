package com.stocker.cartservice.repository;

import com.stocker.cartservice.repository.entity.CartEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CartRepository extends R2dbcRepository<CartEntity, String> {
    Mono<CartEntity> findByUserId(String userId);
}