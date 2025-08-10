package com.stocker.dispatchservice.service.dto;

import com.stocker.dispatchservice.domain.DispatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchDTO {
    private String id;
    private String orderId;
    private DispatchStatus status;
    private String trackingNumber;
    private String carrier;
    private Instant dispatchedAt;
    private Instant createdAt;
    private Instant updatedAt;
}