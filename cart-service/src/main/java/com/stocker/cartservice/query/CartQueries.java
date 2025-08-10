package com.stocker.cartservice.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to find a cart by its ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindCartQuery {
    private String cartId;
}

/**
 * Query to find a cart by user ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindCartByUserIdQuery {
    private String userId;
}

/**
 * Query to find all items in a cart
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindCartItemsQuery {
    private String cartId;
}

/**
 * Query to calculate the total amount of a cart
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculateCartTotalQuery {
    private String cartId;
}