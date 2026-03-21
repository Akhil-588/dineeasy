-- DineEasy – Database Schema + Sample Data

CREATE DATABASE IF NOT EXISTS dineeasy;
USE dineeasy;

CREATE TABLE IF NOT EXISTS users (
    id       INT          AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role     VARCHAR(10)  NOT NULL DEFAULT 'USER'  -- 'USER' | 'ADMIN'
);

CREATE TABLE IF NOT EXISTS products (
    id       INT            AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100)   NOT NULL,
    price    DECIMAL(10,2)  NOT NULL,
    quantity INT            NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS orders (
    id           INT            AUTO_INCREMENT PRIMARY KEY,
    user_id      INT            NOT NULL,
    total_amount DECIMAL(10,2)  NOT NULL,
    order_date   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id         INT            AUTO_INCREMENT PRIMARY KEY,
    order_id   INT            NOT NULL,
    product_id INT            NOT NULL,
    quantity   INT            NOT NULL,
    price      DECIMAL(10,2)  NOT NULL,
    FOREIGN KEY (order_id)   REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Sample data (passwords are plain text for demo — use BCrypt in production)
INSERT INTO users (username, password, role) VALUES
('admin',   'admin123',   'ADMIN'),
('alice',   'alice123',   'USER'),
('bob',     'bob123',     'USER'),
('charlie', 'charlie123', 'USER');

INSERT INTO products (name, price, quantity) VALUES
('Margherita Pizza',    199.00, 50),
('Chicken Burger',      149.00, 60),
('Paneer Tikka',        179.00, 40),
('Veg Fried Rice',      129.00, 70),
('Pasta Arrabbiata',    159.00, 45),
('Chocolate Lava Cake',  89.00, 30),
('Mango Lassi',          59.00, 80),
('Cold Coffee',          69.00, 75),
('Masala Dosa',          99.00, 55),
('Butter Naan (2 pcs)',  49.00, 100);
