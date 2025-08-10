package com.stocker.deliveryservice.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to find a delivery by its ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindDeliveryQuery {
    private String deliveryId;
}

/**
 * Query to find a delivery by order ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindDeliveryByOrderIdQuery {
    private String orderId;
}

/**
 * Query to find a delivery by tracking number
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindDeliveryByTrackingNumberQuery {
    private String trackingNumber;
}

/**
 * Query to find deliveries by status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindDeliveriesByStatusQuery {
    private String status;
}