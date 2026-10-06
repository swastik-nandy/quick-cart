ALTER TABLE identity.users
    ADD COLUMN password_hash VARCHAR(255),
    ADD COLUMN mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE identity.users
SET password_hash = 'local-dev-disabled'
WHERE password_hash IS NULL;

ALTER TABLE identity.users
    ALTER COLUMN password_hash SET NOT NULL;

ALTER TABLE identity.authentication_identities
    DROP CONSTRAINT chk_auth_identity_provider;

ALTER TABLE identity.authentication_identities
    ADD CONSTRAINT chk_auth_identity_provider
        CHECK (provider IN ('EMAIL_PASSWORD', 'GOOGLE'));

CREATE SCHEMA IF NOT EXISTS commerce;

CREATE TABLE commerce.stores (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    area VARCHAR(120) NOT NULL,
    open BOOLEAN NOT NULL DEFAULT TRUE,
    eta_minutes INTEGER NOT NULL DEFAULT 5
);

CREATE TABLE commerce.products (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL REFERENCES commerce.stores(id),
    name VARCHAR(160) NOT NULL,
    category VARCHAR(80) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE commerce.cart_items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES commerce.products(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    UNIQUE (user_id, product_id)
);

CREATE TABLE commerce.orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES identity.users(id),
    store_id BIGINT NOT NULL REFERENCES commerce.stores(id),
    status VARCHAR(30) NOT NULL,
    delivery_address VARCHAR(500) NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL,
    delivery_fee NUMERIC(10, 2) NOT NULL,
    total NUMERIC(10, 2) NOT NULL,
    eta_minutes INTEGER NOT NULL,
    placed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE commerce.order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES commerce.orders(id) ON DELETE CASCADE,
    product_name VARCHAR(160) NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    line_total NUMERIC(10, 2) NOT NULL
);

CREATE INDEX idx_products_category ON commerce.products(category);
CREATE INDEX idx_cart_items_user ON commerce.cart_items(user_id);
CREATE INDEX idx_orders_user ON commerce.orders(user_id, placed_at DESC);

INSERT INTO commerce.stores (name, area, open, eta_minutes)
VALUES
    ('QuickCart Velachery', 'Velachery, Chennai', TRUE, 5),
    ('QuickCart T Nagar', 'T Nagar, Chennai', TRUE, 6);

INSERT INTO commerce.products (store_id, name, category, image_url, price, stock_quantity, active)
VALUES
    (1, 'Aavin Milk 500ml', 'Dairy', 'https://images.unsplash.com/photo-1563636619-e9143da7973b?auto=format&fit=crop&w=500&q=80', 28.00, 90, TRUE),
    (1, 'Brown Bread', 'Bakery', 'https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=500&q=80', 45.00, 40, TRUE),
    (1, 'Banana 6 pcs', 'Fresh', 'https://images.unsplash.com/photo-1603833665858-e61d17a86224?auto=format&fit=crop&w=500&q=80', 55.00, 65, TRUE),
    (1, 'Classic Salted Chips', 'Snacks', 'https://images.unsplash.com/photo-1566478989037-eec170784d0b?auto=format&fit=crop&w=500&q=80', 20.00, 120, TRUE),
    (1, 'Tomato 500g', 'Fresh', 'https://images.unsplash.com/photo-1592924357228-91a4daadcfea?auto=format&fit=crop&w=500&q=80', 32.00, 70, TRUE),
    (2, 'Filter Coffee Powder 200g', 'Grocery', 'https://images.unsplash.com/photo-1447933601403-0c6688de566e?auto=format&fit=crop&w=500&q=80', 145.00, 38, TRUE),
    (2, 'Idli Batter 1kg', 'Fresh', 'https://images.unsplash.com/photo-1668236543090-82eba5ee5976?auto=format&fit=crop&w=500&q=80', 75.00, 30, TRUE),
    (2, 'Coconut Water', 'Beverages', 'https://images.unsplash.com/photo-1553530666-ba11a7da3888?auto=format&fit=crop&w=500&q=80', 45.00, 60, TRUE),
    (2, 'Dishwash Gel 500ml', 'Home Care', 'https://images.unsplash.com/photo-1585421514284-efb74c2b69ba?auto=format&fit=crop&w=500&q=80', 110.00, 25, TRUE),
    (2, 'Dark Chocolate Bar', 'Snacks', 'https://images.unsplash.com/photo-1606312619070-d48b4c652a52?auto=format&fit=crop&w=500&q=80', 99.00, 55, TRUE);
