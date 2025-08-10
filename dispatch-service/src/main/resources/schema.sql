CREATE TABLE IF NOT EXISTS dispatches (
    id VARCHAR(255) PRIMARY KEY,
    order_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    tracking_number VARCHAR(255) NOT NULL,
    carrier VARCHAR(255) NOT NULL,
    dispatched_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_dispatches_order_id ON dispatches(order_id);
CREATE INDEX IF NOT EXISTS idx_dispatches_tracking_number ON dispatches(tracking_number);
CREATE INDEX IF NOT EXISTS idx_dispatches_carrier ON dispatches(carrier);