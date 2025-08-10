package com.stocker.orderservice.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("order_items")
public class OrderItemEntity {
    
    @Id
    private String id;
    
    @Column("order_id")
    private String orderId;
    
    @Column("product_id")
    private String productId;
    
    @Column("product_name")
    private String productName;
    
    @Column("quantity")
    private int quantity;
    
    @Column("unit_price")
    private BigDecimal unitPrice;
    
    @Column("subtotal")
    private BigDecimal subtotal;
}