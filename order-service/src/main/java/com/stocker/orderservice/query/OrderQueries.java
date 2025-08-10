package com.stocker.orderservice.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to find an order by its ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindOrderQuery {
    private String orderId;
}

/**
 * Query to find orders by user ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindOrdersByUserIdQuery {
    private String userId;
}

/**
 * Query to find all items in an order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindOrderItemsQuery {
    private String orderId;
}

/**
 * Query to find orders by status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindOrdersByStatusQuery {
    private String status;
}