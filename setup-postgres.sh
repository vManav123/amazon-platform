#!/bin/bash

# Script to set up PostgreSQL databases for the Amazon Platform microservices

echo "Setting up PostgreSQL databases for Amazon Platform..."

# Check if PostgreSQL is running
if ! pg_isready -q; then
    echo "Error: PostgreSQL is not running. Please start PostgreSQL and try again."
    exit 1
fi

# Create databases for each microservice
for DB in cart_db payment_db order_db dispatch_db delivery_db
do
    echo "Creating database: $DB"
    psql -U postgres -c "DROP DATABASE IF EXISTS $DB;" 2>/dev/null
    psql -U postgres -c "CREATE DATABASE $DB;" 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo "Database $DB created successfully."
    else
        echo "Error creating database $DB. Make sure PostgreSQL is running and user 'postgres' has appropriate permissions."
        exit 1
    fi
done

echo "All databases created successfully!"
echo "You can now start the microservices with PostgreSQL."