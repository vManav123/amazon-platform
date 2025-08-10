package com.stocker.orderservice.repository.entity;

import com.stocker.orderservice.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class OrderEntity {
    
    @Id
    private String id;
    
    @Column("user_id")
    private String userId;
    
    @Column("cart_id")
    private String cartId;
    
    @Column("total_amount")
    private BigDecimal totalAmount;
    
    @Column("shipping_address")
    private String shippingAddress;
    
    @Column("status")
    private String status;
    
    @Column("created_at")
    private Instant createdAt;
    
    @Column("updated_at")
    private Instant updatedAt;
}