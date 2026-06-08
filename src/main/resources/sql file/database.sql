/**
 * Author:  admin
 * Created: Jun 6, 2026
 */

CREATE DATABASE restaurant_db;
USE restaurant_db;

CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE menu_items (
    menu_item_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    category VARCHAR(50) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    table_number VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_time DATETIME,
    total_amount DOUBLE NOT NULL DEFAULT 0
);

CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id)
);

CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    amount DOUBLE NOT NULL,
    tip_amount DOUBLE NOT NULL DEFAULT 0,
    method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    payment_timestamp DATETIME,
    transaction_id VARCHAR(50),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

CREATE TABLE inventory_items (
    inventory_item_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    unit VARCHAR(30) NOT NULL,
    cost_per_unit DOUBLE NOT NULL DEFAULT 0,
    reorder_level INT NOT NULL DEFAULT 0,
    supplier VARCHAR(100),
    last_restocked_date DATE,
    expiry_date DATE
);

CREATE TABLE menu_item_ingredients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    menu_item_id INT NOT NULL,
    inventory_item_id INT NOT NULL,
    quantity_required DOUBLE NOT NULL DEFAULT 1,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id),
    FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(inventory_item_id)
);

CREATE TABLE food_waste (
    food_waste_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    quantity DOUBLE NOT NULL,
    unit VARCHAR(30) NOT NULL,
    reason VARCHAR(100) NOT NULL,
    estimated_cost DOUBLE NOT NULL DEFAULT 0,
    recorded_date DATETIME,
    recorded_by VARCHAR(50),
    category VARCHAR(50) NOT NULL
);

INSERT INTO users (username, password, role) VALUES
('manager', 'manager123', 'MANAGER');
