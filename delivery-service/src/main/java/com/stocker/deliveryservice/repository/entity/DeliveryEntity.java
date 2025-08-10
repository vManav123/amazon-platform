package com.stocker.deliveryservice.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("deliveries")
public class DeliveryEntity {
    
    @Id
    @Column("delivery_id")
    private String deliveryId;
    
    @Column("dispatch_id")
    private String dispatchId;
    
    @Column("order_id")
    private String orderId;
    
    @Column("tracking_number")
    private String trackingNumber;
    
    @Column("delivery_address")
    private String deliveryAddress;
    
    @Column("status")
    private String status;
    
    @Column("delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column("receiver_name")
    private String receiverName;
    
    @Column("delivery_notes")
    private String deliveryNotes;
}