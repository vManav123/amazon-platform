package com.stocker.dispatchservice.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("dispatches")
public class DispatchEntity {
    
    @Id
    private String id;
    
    @Column("order_id")
    private String orderId;
    
    @Column("status")
    private String status;
    
    @Column("tracking_number")
    private String trackingNumber;
    
    @Column("carrier")
    private String carrier;
    
    @Column("dispatched_at")
    private Instant dispatchedAt;
    
    @Column("created_at")
    private Instant createdAt;
    
    @Column("updated_at")
    private Instant updatedAt;
}