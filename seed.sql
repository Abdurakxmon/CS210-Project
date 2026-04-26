-- seed.sql updated
USE car_rental_system;

-- Clear old data
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE cash_transactions;
TRUNCATE TABLE payments;
TRUNCATE TABLE bill_items;
TRUNCATE TABLE bills;
TRUNCATE TABLE notifications;
TRUNCATE TABLE vehicle_reservations;
TRUNCATE TABLE members;
TRUNCATE TABLE receptionists;
TRUNCATE TABLE accounts;
TRUNCATE TABLE persons;
TRUNCATE TABLE vehicles;
TRUNCATE TABLE barcodes;
TRUNCATE TABLE parking_stalls;
TRUNCATE TABLE locations;
TRUNCATE TABLE car_rental_systems;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. System
INSERT INTO car_rental_systems (name) VALUES ('Global Car Rental');

-- 2. Locations
INSERT INTO locations (system_id, name, city, country) VALUES (1, 'Downtown Branch', 'New York', 'USA');
INSERT INTO locations (system_id, name, city, country) VALUES (1, 'Airport Branch', 'New York', 'USA');

-- 3. Barcodes
INSERT INTO barcodes (barcode, issued_at) VALUES ('BC-001', NOW());
INSERT INTO barcodes (barcode, issued_at) VALUES ('BC-002', NOW());

-- 4. Vehicles
INSERT INTO vehicles (location_id, barcode_id, vehicle_type, car_type, license_number, stock_number, status, model, make, manufacturing_year, mileage)
VALUES (1, 1, 1, 1, 'ABC-123', 'S-001', 1, 'Corolla', 'Toyota', 2022, 5000);
INSERT INTO vehicles (location_id, barcode_id, vehicle_type, car_type, license_number, stock_number, status, model, make, manufacturing_year, mileage)
VALUES (2, 2, 1, 6, 'XYZ-789', 'S-002', 1, 'Model 3', 'Tesla', 2023, 1000);

-- 5. Persons & Accounts
-- Super Admin
INSERT INTO persons (name, email) VALUES ('Admin User', 'admin@rental.com');
INSERT INTO accounts (person_id, username, password_hash, status, role_type) 
VALUES (LAST_INSERT_ID(), 'admin', 'admin123', 1, 4);

-- Receptionist
INSERT INTO persons (name, email) VALUES ('Jane Staff', 'jane@rental.com');
INSERT INTO accounts (person_id, username, password_hash, status, role_type) 
VALUES (LAST_INSERT_ID(), 'jane_staff', 'password123', 1, 2);

-- Member
INSERT INTO persons (name, email) VALUES ('John Member', 'john@gmail.com');
INSERT INTO accounts (person_id, username, password_hash, status, role_type) 
VALUES (LAST_INSERT_ID(), 'john_member', 'password123', 1, 1);
INSERT INTO members (account_id, driver_license_number, driver_license_expiry) 
VALUES (LAST_INSERT_ID(), 'DL-99999', DATE_ADD(NOW(), INTERVAL 5 YEAR));
