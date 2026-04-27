-- seed.sql for Car Rental System
USE car_rental_system;

-- Clear old data
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE cash_transactions;
TRUNCATE TABLE check_transactions;
TRUNCATE TABLE credit_card_transactions;
TRUNCATE TABLE payments;
TRUNCATE TABLE bill_items;
TRUNCATE TABLE bills;
TRUNCATE TABLE notifications;
TRUNCATE TABLE services;
TRUNCATE TABLE equipment;
TRUNCATE TABLE rental_insurances;
TRUNCATE TABLE additional_drivers;
TRUNCATE TABLE vehicle_reservations;
TRUNCATE TABLE vehicle_logs;
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

-- 1. Car Rental System
INSERT INTO car_rental_systems (name) VALUES ('Antigravity Car Rentals');

-- 2. Locations
INSERT INTO locations (system_id, name, street_address, city, state, zipcode, country) 
VALUES (1, 'Downtown NYC', '123 Broadway', 'New York', 'NY', '10001', 'USA');
INSERT INTO locations (system_id, name, street_address, city, state, zipcode, country) 
VALUES (1, 'JFK Airport', 'Terminal 4', 'Queens', 'NY', '11430', 'USA');
INSERT INTO locations (system_id, name, street_address, city, state, zipcode, country) 
VALUES (1, 'Jersey City Center', '500 Washington Blvd', 'Jersey City', 'NJ', '07310', 'USA');

-- 3. Parking Stalls
INSERT INTO parking_stalls (location_id, stall_number, location_identifier) VALUES (1, 'D-01', 'Floor 1');
INSERT INTO parking_stalls (location_id, stall_number, location_identifier) VALUES (1, 'D-02', 'Floor 1');
INSERT INTO parking_stalls (location_id, stall_number, location_identifier) VALUES (2, 'A-101', 'Garage B');
INSERT INTO parking_stalls (location_id, stall_number, location_identifier) VALUES (2, 'A-102', 'Garage B');
INSERT INTO parking_stalls (location_id, stall_number, location_identifier) VALUES (3, 'J-05', 'Main Lot');

-- 4. Barcodes
INSERT INTO barcodes (barcode, issued_at) VALUES ('BC-TOY-001', NOW());
INSERT INTO barcodes (barcode, issued_at) VALUES ('BC-TES-001', NOW());
INSERT INTO barcodes (barcode, issued_at) VALUES ('BC-FOR-001', NOW());
INSERT INTO barcodes (barcode, issued_at) VALUES ('BC-BMW-001', NOW());
INSERT INTO barcodes (barcode, issued_at) VALUES ('BC-HON-001', NOW());

-- 5. Vehicles
-- Toyota Corolla (Economy)
INSERT INTO vehicles (location_id, parking_stall_id, barcode_id, vehicle_type, car_type, license_number, stock_number, passenger_capacity, has_sunroof, status, model, make, manufacturing_year, mileage)
VALUES (1, 1, 1, 1, 1, 'NY-1234', 'S001', 5, FALSE, 1, 'Corolla', 'Toyota', 2022, 12500);

-- Tesla Model 3 (Premium)
INSERT INTO vehicles (location_id, parking_stall_id, barcode_id, vehicle_type, car_type, license_number, stock_number, passenger_capacity, has_sunroof, status, model, make, manufacturing_year, mileage)
VALUES (2, 3, 2, 1, 6, 'NY-TESLA', 'S002', 5, TRUE, 1, 'Model 3', 'Tesla', 2023, 2100);

-- Ford F-150 (Truck)
INSERT INTO vehicles (location_id, parking_stall_id, barcode_id, vehicle_type, car_type, license_number, stock_number, passenger_capacity, has_sunroof, status, model, make, manufacturing_year, mileage)
VALUES (1, 2, 3, 2, 4, 'NY-TRUCK', 'S003', 3, FALSE, 1, 'F-150', 'Ford', 2021, 45000);

-- BMW X5 (Luxury SUV)
INSERT INTO vehicles (location_id, parking_stall_id, barcode_id, vehicle_type, car_type, license_number, stock_number, passenger_capacity, has_sunroof, status, model, make, manufacturing_year, mileage)
VALUES (3, 5, 4, 3, 7, 'NJ-BMW-X5', 'S004', 7, TRUE, 1, 'X5', 'BMW', 2023, 500);

-- Honda Odyssey (Van)
INSERT INTO vehicles (location_id, parking_stall_id, barcode_id, vehicle_type, car_type, license_number, stock_number, passenger_capacity, has_sunroof, status, model, make, manufacturing_year, mileage)
VALUES (2, 4, 5, 4, 4, 'NY-VAN-1', 'S005', 8, FALSE, 1, 'Odyssey', 'Honda', 2020, 68000);

-- 6. Persons & Accounts for each role
-- Super Admin
INSERT INTO persons (name, email, phone, city, country) VALUES ('Super Admin', 'admin@antigravity.com', '111-222-3333', 'Boston', 'USA');
INSERT INTO accounts (person_id, username, password_hash, status, role_type) 
VALUES (LAST_INSERT_ID(), 'admin', 'admin123', 1, 4);

-- Receptionist
INSERT INTO persons (name, email, phone, city, country) VALUES ('Sarah Staff', 'sarah@antigravity.com', '222-333-4444', 'New York', 'USA');
INSERT INTO accounts (person_id, username, password_hash, status, role_type) 
VALUES (LAST_INSERT_ID(), 'sarah', 'password123', 1, 2);
INSERT INTO receptionists (account_id, date_joined) VALUES (LAST_INSERT_ID(), NOW());

-- Worker
INSERT INTO persons (name, email, phone, city, country) VALUES ('Willy Worker', 'willy@antigravity.com', '333-444-5555', 'Queens', 'USA');
INSERT INTO accounts (person_id, username, password_hash, status, role_type) 
VALUES (LAST_INSERT_ID(), 'worker', 'password123', 1, 3);

-- Members
INSERT INTO persons (name, email, phone, city, country) VALUES ('John Member', 'john@gmail.com', '444-555-6666', 'Brooklyn', 'USA');
INSERT INTO accounts (person_id, username, password_hash, status, role_type) 
VALUES (LAST_INSERT_ID(), 'member', 'password123', 1, 1);
INSERT INTO members (account_id, driver_license_number, driver_license_expiry) 
VALUES (LAST_INSERT_ID(), 'DL-NY-112233', DATE_ADD(NOW(), INTERVAL 3 YEAR));

INSERT INTO persons (name, email, phone, city, country) VALUES ('Alice Member', 'alice@yahoo.com', '555-666-7777', 'Hoboken', 'USA');
INSERT INTO accounts (person_id, username, password_hash, status, role_type) 
VALUES (LAST_INSERT_ID(), 'alice', 'password123', 1, 1);
INSERT INTO members (account_id, driver_license_number, driver_license_expiry) 
VALUES (LAST_INSERT_ID(), 'DL-NJ-998877', DATE_ADD(NOW(), INTERVAL 4 YEAR));
