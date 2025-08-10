package com.stocker.paymentservice.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to find a payment by its ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindPaymentQuery {
    private String paymentId;
}

/**
 * Query to find payments by order ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindPaymentsByOrderIdQuery {
    private String orderId;
}

/**
 * Query to find a payment by transaction ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindPaymentByTransactionIdQuery {
    private String transactionId;
}