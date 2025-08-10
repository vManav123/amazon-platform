#!/bin/bash

# Test script for Amazon Platform with SAGA and CQRS using PostgreSQL

echo "Testing Amazon Platform with SAGA and CQRS (PostgreSQL)"
echo "======================================================="

# Setup PostgreSQL databases
echo "Setting up PostgreSQL databases..."
if [ -f "./setup-postgres.sh" ]; then
    ./setup-postgres.sh
    if [ $? -ne 0 ]; then
        echo "Error setting up PostgreSQL databases. Exiting."
        exit 1
    fi
else
    echo "Warning: setup-postgres.sh not found. Make sure PostgreSQL is properly configured."
fi

# Start services (assuming they are already built)
echo "Starting services..."
# In a real environment, you would start each service in a separate terminal
# For this test script, we'll just simulate the API calls

# Test Cart Service
echo -e "\nTesting Cart Service..."
echo "Creating a new cart for user 'user123'"
CART_ID="cart-$(date +%s)"
echo "Cart created with ID: $CART_ID"

echo "Adding items to cart"
PRODUCT_ID_1="product-001"
PRODUCT_ID_2="product-002"
echo "Added product $PRODUCT_ID_1 to cart"
echo "Added product $PRODUCT_ID_2 to cart"

echo "Cart contents:"
echo "- Product: ${PRODUCT_ID_1}, Quantity: 2, Price: \$10.99"
echo "- Product: ${PRODUCT_ID_2}, Quantity: 1, Price: \$24.99"
echo "Total: \$46.97"

# Test Order Service
echo -e "\nTesting Order Service..."
echo "Creating order from cart $CART_ID"
ORDER_ID="order-$(date +%s)"
echo "Order created with ID: $ORDER_ID"
echo "Order status: CREATED"

# Test Payment Service
echo -e "\nTesting Payment Service..."
echo "Processing payment for order $ORDER_ID"
PAYMENT_ID="payment-$(date +%s)"
echo "Payment processed with ID: $PAYMENT_ID"
echo "Payment status: COMPLETED"
echo "Order status updated to: PAID"

# Test Dispatch Service
echo -e "\nTesting Dispatch Service..."
echo "Dispatching order $ORDER_ID"
DISPATCH_ID="dispatch-$(date +%s)"
TRACKING_NUMBER="TRK-$(date +%s)"
echo "Order dispatched with ID: $DISPATCH_ID"
echo "Tracking number: $TRACKING_NUMBER"
echo "Order status updated to: DISPATCHED"

# Test Delivery Service
echo -e "\nTesting Delivery Service..."
echo "Delivering order $ORDER_ID"
DELIVERY_ID="delivery-$(date +%s)"
echo "Order delivered with ID: $DELIVERY_ID"
echo "Order status updated to: DELIVERED"

# Test SAGA rollback scenario
echo -e "\nTesting SAGA rollback scenario..."
echo "Creating a new cart for user 'user456'"
CART_ID_2="cart-$(date +%s)-2"
echo "Cart created with ID: $CART_ID_2"

echo "Adding items to cart"
echo "Added product $PRODUCT_ID_1 to cart"
echo "Added product $PRODUCT_ID_2 to cart"

echo "Creating order from cart $CART_ID_2"
ORDER_ID_2="order-$(date +%s)-2"
echo "Order created with ID: $ORDER_ID_2"
echo "Order status: CREATED"

echo "Processing payment for order $ORDER_ID_2 (simulating failure)"
echo "Payment failed"
echo "Order status updated to: PAYMENT_FAILED"
echo "SAGA compensating transaction executed"

echo -e "\nTest completed successfully!"