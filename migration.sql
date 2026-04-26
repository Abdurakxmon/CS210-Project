-- migration.sql
USE car_rental_system;

-- Add soft delete column to vehicles
ALTER TABLE vehicles ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

-- Add soft delete column to accounts
ALTER TABLE accounts ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

-- Seed Super Admin
INSERT INTO persons (name, email, phone, city, country) 
VALUES ('Super Admin', 'admin@carrental.com', '555-9999', 'Boston', 'USA');

INSERT INTO accounts (person_id, username, password_hash, status, role_type) 
VALUES (LAST_INSERT_ID(), 'admin', 'admin123', 1, 4);
