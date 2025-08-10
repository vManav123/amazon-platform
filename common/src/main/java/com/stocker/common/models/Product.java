package com.stocker.common.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String productId;
    private String name;
    private String description;
    private BigDecimal price;
    private int availableQuantity;
}