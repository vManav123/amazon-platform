package com.stocker.dispatchservice.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to find a dispatch by its ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindDispatchQuery {
    private String dispatchId;
}

/**
 * Query to find a dispatch by order ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindDispatchByOrderIdQuery {
    private String orderId;
}

/**
 * Query to find a dispatch by tracking number
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindDispatchByTrackingNumberQuery {
    private String trackingNumber;
}

/**
 * Query to find dispatches by carrier
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindDispatchesByCarrierQuery {
    private String carrier;
}