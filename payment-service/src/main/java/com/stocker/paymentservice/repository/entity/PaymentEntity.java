package com.stocker.paymentservice.repository.entity;

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
@Table("payments")
public class PaymentEntity {
    
    @Id
    private String id;
    
    @Column("order_id")
    private String orderId;
    
    @Column("amount")
    private BigDecimal amount;
    
    @Column("status")
    private String status;
    
    @Column("payment_method")
    private String paymentMethod;
    
    @Column("transaction_id")
    private String transactionId;
    
    @Column("created_at")
    private Instant createdAt;
    
    @Column("updated_at")
    private Instant updatedAt;
}