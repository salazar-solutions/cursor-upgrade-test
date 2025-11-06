-- Rollback script for dummy data
-- Schema: cursordb

-- Set search path to use cursordb schema
SET search_path TO cursordb;

DELETE FROM inventory WHERE product_id IN (
    '660e8400-e29b-41d4-a716-446655440000',
    '660e8400-e29b-41d4-a716-446655440001',
    '660e8400-e29b-41d4-a716-446655440002'
);

DELETE FROM products WHERE id IN (
    '660e8400-e29b-41d4-a716-446655440000',
    '660e8400-e29b-41d4-a716-446655440001',
    '660e8400-e29b-41d4-a716-446655440002'
);

DELETE FROM payments WHERE id IN (
    '880e8400-e29b-41d4-a716-446655440000',
    '880e8400-e29b-41d4-a716-446655440001'
);

DELETE FROM orders WHERE id IN (
    '770e8400-e29b-41d4-a716-446655440000',
    '770e8400-e29b-41d4-a716-446655440001'
);

DELETE FROM user_roles WHERE user_id IN (
    '550e8400-e29b-41d4-a716-446655440000',
    '550e8400-e29b-41d4-a716-446655440001'
);

DELETE FROM users WHERE id IN (
    '550e8400-e29b-41d4-a716-446655440000',
    '550e8400-e29b-41d4-a716-446655440001'
);

