# Migration from H2 to PostgreSQL Reactive

## Overview
This document summarizes the changes made to migrate the Amazon shopping platform from H2 in-memory database to PostgreSQL with reactive support.

## Changes Made

### 1. Parent POM Updates
- Added PostgreSQL and R2DBC-PostgreSQL version properties:
  ```xml
  <postgresql.version>42.7.2</postgresql.version>
  <r2dbc-postgresql.version>1.0.4.RELEASE</r2dbc-postgresql.version>
  ```
- Added PostgreSQL dependencies to dependencyManagement:
  ```xml
  <!-- PostgreSQL Database -->
  <dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
      <version>${postgresql.version}</version>
      <scope>runtime</scope>
  </dependency>
  <dependency>
      <groupId>io.r2dbc</groupId>
      <artifactId>r2dbc-postgresql</artifactId>
      <version>${r2dbc-postgresql.version}</version>
  </dependency>
  ```

### 2. Microservice POM Updates
Updated the following microservices' pom.xml files:
- cart-service
- payment-service
- order-service
- dispatch-service

Changes in each microservice:
- Changed H2 dependencies scope to `test`
- Added PostgreSQL dependencies:
  ```xml
  <!-- PostgreSQL Database -->
  <dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
      <scope>runtime</scope>
  </dependency>
  <dependency>
      <groupId>io.r2dbc</groupId>
      <artifactId>r2dbc-postgresql</artifactId>
  </dependency>
  ```

### 3. Application Properties Updates
Updated application.properties in all microservices:
- cart-service
- payment-service
- order-service
- dispatch-service
- delivery-service

Changes in each microservice:
- Replaced H2 connection settings with PostgreSQL:
  ```properties
  # R2DBC Configuration for PostgreSQL
  spring.r2dbc.url=r2dbc:postgresql://localhost:5432/{service_name}_db
  spring.r2dbc.username=postgres
  spring.r2dbc.password=postgres
  spring.r2dbc.properties.ssl=false
  ```
- Added connection pool configuration:
  ```properties
  # Connection Pool Configuration
  spring.r2dbc.pool.initial-size=5
  spring.r2dbc.pool.max-size=20
  ```
- Disabled H2 console (where applicable):
  ```properties
  # H2 Console Configuration (disabled for production)
  spring.h2.console.enabled=false
  ```

### 4. Schema Compatibility
- Verified that all schema.sql files are compatible with PostgreSQL
- No changes were needed as the SQL syntax used is standard and works with PostgreSQL

## Prerequisites for Running the Application
1. PostgreSQL server running on localhost:5432
2. The following databases created:
   - cart_db
   - payment_db
   - order_db
   - dispatch_db
   - delivery_db
3. User 'postgres' with password 'postgres' having access to these databases

## Next Steps
1. Set up PostgreSQL in your environment
2. Create the required databases
3. Run the application to verify the migration