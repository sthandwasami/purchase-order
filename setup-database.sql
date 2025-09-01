-- MySQL Database Setup Script
-- Run this script in your MySQL client to create the database

CREATE DATABASE IF NOT EXISTS purchase_order;
USE purchase_order;

-- Grant privileges (adjust username/password as needed)
-- GRANT ALL PRIVILEGES ON purchase_order.* TO 'root'@'localhost';
-- FLUSH PRIVILEGES;

-- The application will automatically create tables using JPA/Hibernate