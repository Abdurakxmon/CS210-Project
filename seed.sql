-- seed.sql for Car Rental System
CREATE DATABASE IF NOT EXISTS car_rental_system;
USE car_rental_system;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE return_inspections;
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

INSERT INTO car_rental_systems (id, name) VALUES
(1, 'UzAuto Rental'),
(2, 'Tashkent City Cars');

INSERT INTO locations (id, system_id, name, street_address, city, state, zipcode, country) VALUES
(1, 1, 'Toshkent Markaz', 'Amir Temur kochasi, 10', 'Toshkent', 'Toshkent', '100000', 'Uzbekiston'),
(2, 1, 'Toshkent Aeroport', 'Aeroport yoli', 'Toshkent', 'Toshkent', '100167', 'Uzbekiston'),
(3, 2, 'Samarqand Vokzal', 'Beruniy kochasi, 5', 'Samarqand', 'Samarqand', '140100', 'Uzbekiston'),
(4, 2, 'Buxoro Eski Shahar', 'B. Naqshband kochasi', 'Buxoro', 'Buxoro', '200100', 'Uzbekiston');

INSERT INTO parking_stalls (id, location_id, stall_number, location_identifier) VALUES
(1, 1, 'T-01', 'Qavat 1'),
(2, 1, 'T-02', 'Qavat 1'),
(3, 2, 'A-10', 'Terminal 2'),
(4, 2, 'A-11', 'Terminal 2'),
(5, 3, 'S-01', 'Asosiy Totxtash Joyi'),
(6, 3, 'S-02', 'Asosiy Totxtash Joyi'),
(7, 4, 'B-01', 'Mehmonxona Oldi'),
(8, 4, 'B-02', 'Mehmonxona Oldi'),
(9, 1, 'T-03', 'Qavat 2'),
(10, 2, 'A-12', 'Terminal 2');

INSERT INTO barcodes (id, barcode, issued_at) VALUES
(1, 'BC-NEX-001', NOW()),
(2, 'BC-COB-001', NOW()),
(3, 'BC-GEN-001', NOW()),
(4, 'BC-MAL-001', NOW()),
(5, 'BC-TRA-001', NOW()),
(6, 'BC-TBL-001', NOW()),
(7, 'BC-SPA-001', NOW()),
(8, 'BC-DAM-001', NOW()),
(9, 'BC-TAH-001', NOW()),
(10, 'BC-CAP-001', NOW());

INSERT INTO vehicles (
    id, location_id, parking_stall_id, barcode_id, vehicle_type, car_type, license_number,
    stock_number, passenger_capacity, has_sunroof, status, model, make, manufacturing_year,
    mileage, is_active, price_per_day, image_path, transmission_type, fuel_type, fuel_level
) VALUES
(1, 1, 1, 1, 1, 1, '01 A 777 AA', 'V001', 5, 0, 1, 'Nexia 3', 'Chevrolet', 2022, 15000, 1, 10.00, 'images/chevrolet-nexia-3.jpg', 1, 1, 80),
(2, 1, 2, 2, 1, 2, '01 B 123 BB', 'V002', 5, 0, 1, 'Cobalt', 'Chevrolet', 2023, 5000, 1, 12.00, 'images/chevrolet-cobalt.jpg', 1, 1, 90),
(3, 2, 3, 3, 1, 3, '01 C 456 CC', 'V003', 5, 1, 1, 'Gentra', 'Chevrolet', 2022, 12000, 1, 13.00, 'images/chevrolet-gentra.jpg', 2, 1, 100),
(4, 2, 4, 4, 1, 6, '01 M 001 MM', 'V004', 5, 1, 1, 'Malibu 2', 'Chevrolet', 2023, 2000, 1, 20.00, 'images/chevrolet-malibu.jpg', 1, 3, 70),
(5, 3, 5, 5, 3, 4, '10 X 500 XX', 'V005', 5, 1, 1, 'Tracker 2', 'Chevrolet', 2023, 3000, 1, 18.00, 'images/chevrolet-tracker.jpg', 1, 1, 95),
(6, 3, 6, 6, 3, 7, '10 Z 999 ZZ', 'V006', 7, 1, 1, 'Trailblazer', 'Chevrolet', 2021, 45000, 1, 24.00, 'images/chevrolet-trailblazer.jpg', 1, 2, 65),
(7, 4, 7, 7, 1, 1, '80 S 100 SS', 'V007', 4, 0, 1, 'Spark', 'Chevrolet', 2020, 60000, 1, 9.00, 'images/chevrolet-spark.jpg', 2, 1, 75),
(8, 4, 8, 8, 4, 4, '80 D 200 DD', 'V008', 7, 0, 1, 'Damas', 'Chevrolet', 2022, 25000, 1, 11.00, 'images/chevrolet-damas.jpg', 2, 1, 60),
(9, 1, 9, 9, 3, 7, '01 T 888 TT', 'V009', 7, 1, 1, 'Tahoe', 'Chevrolet', 2023, 1000, 1, 35.00, 'images/chevrolet-tahoe.jpg', 1, 1, 85),
(10, 2, 10, 10, 3, 5, '01 K 333 KK', 'V010', 7, 1, 1, 'Captiva', 'Chevrolet', 2022, 1111, 1, 22.00, 'images/chevrolet-captiva.jpg', 1, 1, 100);

INSERT INTO persons (id, name, street_address, city, state, zipcode, country, email, phone, birth_date) VALUES
(1, 'Alisher Navoiy', 'Admin street 1', 'Toshkent', NULL, NULL, 'Uzbekiston', 'admin@uzrental.uz', '+998901234567', '1990-01-01'),
(2, 'Nigora Umidova', 'Staff street 2', 'Toshkent', NULL, NULL, 'Uzbekiston', 'nigora@uzrental.uz', '+998912345678', '1992-02-02'),
(3, 'Baxtiyor Gulomov', 'Worker street 3', 'Toshkent', NULL, NULL, 'Uzbekiston', 'baxtiyor@uzrental.uz', '+998934567890', '1991-03-03'),
(4, 'Jasur Alimov', 'Customer street 4', 'Samarqand', NULL, NULL, 'Uzbekiston', 'jasur@gmail.com', '+998941112233', '1998-06-15'),
(5, 'Malika Karimova', 'Customer street 5', 'Buxoro', NULL, NULL, 'Uzbekiston', 'malika@mail.ru', '+998971110022', '1997-08-20');

INSERT INTO accounts (id, person_id, username, password_hash, status, role_type, is_active) VALUES
(1, 1, 'admin', 'password123', 1, 4, 1),
(2, 2, 'nigora', 'password123', 1, 2, 1),
(3, 3, 'worker', 'password123', 1, 3, 1),
(4, 4, 'jama', 'pass123', 1, 1, 1),
(5, 5, 'malika', 'password123', 1, 1, 1);

INSERT INTO receptionists (id, account_id, date_joined) VALUES
(1, 2, NOW());

INSERT INTO members (id, account_id, driver_license_number, driver_license_expiry) VALUES
(1, 4, 'UZ-AA1234567', DATE_ADD(NOW(), INTERVAL 3 YEAR)),
(2, 5, 'UZ-BB7654321', DATE_ADD(NOW(), INTERVAL 4 YEAR));

INSERT INTO vehicle_reservations (
    id, reservation_number, member_id, vehicle_id, creation_date, pickup_date, status, due_date,
    return_date, pickup_location_id, return_location_id, processed_by_account_id
) VALUES
(1, 'RES-20260501-000001', 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), 3, DATE_ADD(NOW(), INTERVAL 4 DAY), NULL, 1, 2, NULL),
(2, 'RES-20260501-000002', 1, 9, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 2, DATE_ADD(NOW(), INTERVAL 2 DAY), NULL, 1, 2, 2),
(3, 'RES-20260501-000003', 2, 10, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), 4, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 2, 1, 3);

INSERT INTO bills (id, reservation_id, total_amount) VALUES
(1, 1, 40.00),
(2, 2, 105.00),
(3, 3, 184.00);

INSERT INTO bill_items (bill_id, item_type, amount, service_name) VALUES
(1, 1, 40.00, 'Base Rental Charge (4 days)'),
(2, 1, 105.00, 'Base Rental Charge (3 days)'),
(3, 1, 154.00, 'Base Rental Charge (7 days)'),
(3, 5, 30.00, 'Late Fee');

INSERT INTO payments (id, bill_id, creation_date, amount, status, payment_type) VALUES
(1, 2, NOW(), 105.00, 3, 3),
(2, 3, NOW(), 184.00, 3, 1);

INSERT INTO cash_transactions (id, payment_id, cash_tendered) VALUES
(1, 1, 105.00);

INSERT INTO notifications (reservation_id, notification_type, created_on, content) VALUES
(1, 1, NOW(), 'Reservation RES-20260501-000001 confirmed.'),
(2, 4, NOW(), 'Pickup reminder for reservation RES-20260501-000002.'),
(3, 7, NOW(), 'Vehicle returned successfully.');

INSERT INTO vehicle_logs (vehicle_id, log_type, description, creation_date, account_id) VALUES
(1, 6, 'Reservation created.', NOW(), 4),
(9, 6, 'Vehicle picked up for reservation RES-20260501-000002.', NOW(), 2),
(10, 3, 'Vehicle returned and inspected.', NOW(), 3);
