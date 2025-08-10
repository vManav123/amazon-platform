package com.stocker.cartservice.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("carts")
public class CartEntity {
    @Id
    private String cartId;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}