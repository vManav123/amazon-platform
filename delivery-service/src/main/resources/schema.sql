CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id VARCHAR(255) PRIMARY KEY,
    dispatch_id VARCHAR(255) NOT NULL,
    order_id VARCHAR(255) NOT NULL,
    tracking_number VARCHAR(255) NOT NULL,
    delivery_address VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    delivered_at TIMESTAMP,
    receiver_name VARCHAR(255),
    delivery_notes VARCHAR(1000)
);

CREATE INDEX IF NOT EXISTS idx_deliveries_order_id ON deliveries(order_id);
CREATE INDEX IF NOT EXISTS idx_deliveries_tracking_number ON deliveries(tracking_number);
CREATE INDEX IF NOT EXISTS idx_deliveries_status ON deliveries(status);