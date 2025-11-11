-- 초기 데이터 삽입
-- 테스트 및 데모용 샘플 데이터

-- 사용자 데이터
-- 비밀번호는 BCrypt로 암호화된 'password123'
INSERT INTO users (username, password, email, full_name, active, role) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@example.com', 'Admin User', true, 'ROLE_ADMIN'),
('john_doe', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'john@example.com', 'John Doe', true, 'ROLE_USER'),
('jane_smith', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'jane@example.com', 'Jane Smith', true, 'ROLE_USER'),
('bob_wilson', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'bob@example.com', 'Bob Wilson', true, 'ROLE_USER'),
('alice_brown', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'alice@example.com', 'Alice Brown', false, 'ROLE_USER');

-- 상품 데이터
INSERT INTO products (name, description, price, stock_quantity, category, status) VALUES
('Laptop Pro 15', 'High-performance laptop with 15-inch display', 1299.99, 50, 'Electronics', 'AVAILABLE'),
('Wireless Mouse', 'Ergonomic wireless mouse with USB receiver', 29.99, 200, 'Electronics', 'AVAILABLE'),
('USB-C Cable', 'Premium USB-C cable 2 meters', 15.99, 500, 'Electronics', 'AVAILABLE'),
('Desk Chair', 'Comfortable office chair with lumbar support', 249.99, 30, 'Furniture', 'AVAILABLE'),
('Standing Desk', 'Adjustable height standing desk', 599.99, 15, 'Furniture', 'AVAILABLE'),
('Coffee Mug', 'Ceramic coffee mug 350ml', 12.99, 100, 'Kitchen', 'AVAILABLE'),
('Water Bottle', 'Stainless steel water bottle 1L', 24.99, 80, 'Kitchen', 'AVAILABLE'),
('Notebook A4', 'Premium notebook with 200 pages', 8.99, 300, 'Stationery', 'AVAILABLE'),
('Pen Set', 'Set of 5 ballpoint pens', 9.99, 250, 'Stationery', 'AVAILABLE'),
('Backpack', 'Laptop backpack with multiple compartments', 79.99, 60, 'Accessories', 'AVAILABLE'),
('Headphones', 'Noise-cancelling wireless headphones', 199.99, 5, 'Electronics', 'AVAILABLE'),
('Monitor 27"', '4K UHD monitor 27-inch', 449.99, 0, 'Electronics', 'OUT_OF_STOCK');

-- 주문 데이터
-- 주문 1: John Doe의 주문
INSERT INTO orders (user_id, status, total_amount, shipping_address) VALUES
(1, 'DELIVERED', 1345.97, '123 Main St, New York, NY 10001');

INSERT INTO order_items (order_id, product_id, quantity, price, subtotal) VALUES
(1, 1, 1, 1299.99, 1299.99),  -- Laptop Pro 15
(1, 2, 1, 29.99, 29.99),       -- Wireless Mouse
(1, 3, 1, 15.99, 15.99);       -- USB-C Cable

-- 주문 2: Jane Smith의 주문
INSERT INTO orders (user_id, status, total_amount, shipping_address) VALUES
(2, 'SHIPPED', 849.98, '456 Oak Ave, Los Angeles, CA 90001');

INSERT INTO order_items (order_id, product_id, quantity, price, subtotal) VALUES
(2, 4, 1, 249.99, 249.99),     -- Desk Chair
(2, 5, 1, 599.99, 599.99);     -- Standing Desk

-- 주문 3: John Doe의 두 번째 주문
INSERT INTO orders (user_id, status, total_amount, shipping_address) VALUES
(1, 'PENDING', 237.96, '123 Main St, New York, NY 10001');

INSERT INTO order_items (order_id, product_id, quantity, price, subtotal) VALUES
(3, 7, 2, 24.99, 49.98),       -- Water Bottle x2
(3, 8, 10, 8.99, 89.90),       -- Notebook A4 x10
(3, 9, 10, 9.99, 99.90);       -- Pen Set x10

-- 주문 4: Bob Wilson의 주문
INSERT INTO orders (user_id, status, total_amount, shipping_address) VALUES
(3, 'CONFIRMED', 279.98, '789 Elm St, Chicago, IL 60601');

INSERT INTO order_items (order_id, product_id, quantity, price, subtotal) VALUES
(4, 10, 1, 79.99, 79.99),      -- Backpack
(4, 11, 1, 199.99, 199.99);    -- Headphones

-- 주문 5: Jane Smith의 두 번째 주문 (취소된 주문)
INSERT INTO orders (user_id, status, total_amount, shipping_address) VALUES
(2, 'CANCELLED', 77.94, '456 Oak Ave, Los Angeles, CA 90001');

INSERT INTO order_items (order_id, product_id, quantity, price, subtotal) VALUES
(5, 6, 6, 12.99, 77.94);       -- Coffee Mug x6

-- 주석: MyBatis를 통해 이 데이터들을 조회하고 조작할 수 있습니다
-- - Association을 사용하여 주문과 사용자 정보를 함께 조회
-- - Collection을 사용하여 주문과 주문 상세 항목들을 함께 조회
-- - 동적 SQL을 사용하여 다양한 조건으로 데이터 검색
