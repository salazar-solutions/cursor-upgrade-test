-- Insert dummy/test data
-- Schema: cursordb

-- Set search path to use cursordb schema
SET search_path TO cursordb;

-- Insert test users
INSERT INTO users (id, username, email, password_hash, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440001', 'user1', 'user1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440002', 'existinguser', 'existing@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert user roles
INSERT INTO user_roles (user_id, role) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'ADMIN'),
('550e8400-e29b-41d4-a716-446655440000', 'USER'),
('550e8400-e29b-41d4-a716-446655440001', 'USER'),
('550e8400-e29b-41d4-a716-446655440002', 'USER')
ON CONFLICT (user_id, role) DO NOTHING;

-- Insert test products
INSERT INTO products (id, sku, name, description, price, available_qty, created_at, updated_at) VALUES
('660e8400-e29b-41d4-a716-446655440000', 'SKU-001', 'Test Product 1', 'Description for product 1', 99.99, 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('660e8400-e29b-41d4-a716-446655440001', 'SKU-002', 'Test Product 2', 'Description for product 2', 149.99, 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('660e8400-e29b-41d4-a716-446655440002', 'SKU-003', 'Test Product 3', 'Description for product 3', 199.99, 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert inventory records
INSERT INTO inventory (product_id, reserved_qty, available_qty) VALUES
('660e8400-e29b-41d4-a716-446655440000', 0, 100),
('660e8400-e29b-41d4-a716-446655440001', 0, 50),
('660e8400-e29b-41d4-a716-446655440002', 0, 25)
ON CONFLICT (product_id) DO NOTHING;

-- Insert test orders (required for payment test data)
INSERT INTO orders (id, user_id, total_amount, status, payment_id, created_at, updated_at) VALUES
('770e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440001', 199.98, 'PENDING', '880e8400-e29b-41d4-a716-446655440000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('770e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 299.97, 'PENDING', '880e8400-e29b-41d4-a716-446655440001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Insert test payments
INSERT INTO payments (id, order_id, status, provider_ref, amount, created_at) VALUES
('880e8400-e29b-41d4-a716-446655440000', '770e8400-e29b-41d4-a716-446655440000', 'SUCCESS', 'PROV-REF-770E8400-1234567890', 199.98, CURRENT_TIMESTAMP),
('880e8400-e29b-41d4-a716-446655440001', '770e8400-e29b-41d4-a716-446655440001', 'PENDING', NULL, 299.97, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

