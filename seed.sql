-- seed.sql for Car Rental System
-- Run schema.sql first, then this file.
CREATE DATABASE IF NOT EXISTS car_rental_system;
USE car_rental_system;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM return_inspections;
DELETE FROM cash_transactions;
DELETE FROM check_transactions;
DELETE FROM credit_card_transactions;
DELETE FROM payments;
DELETE FROM bill_items;
DELETE FROM bills;
DELETE FROM notifications;
DELETE FROM services;
DELETE FROM equipment;
DELETE FROM rental_insurances;
DELETE FROM additional_drivers;
DELETE FROM vehicle_reservations;
DELETE FROM vehicle_logs;
DELETE FROM members;
DELETE FROM receptionists;
DELETE FROM accounts;
DELETE FROM persons;
DELETE FROM vehicles;
DELETE FROM barcodes;
DELETE FROM parking_stalls;
DELETE FROM locations;
DELETE FROM car_rental_systems;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO car_rental_systems (id, name) VALUES
(1, 'UzAuto Rental'),
(2, 'Tashkent City Cars');

INSERT INTO locations (id, system_id, name, street_address, city, state, zipcode, country) VALUES
(1, 1, 'Toshkent Markaz', 'Amir Temur kochasi, 10', 'Toshkent', 'Toshkent', '100000', 'Ozbekiston'),
(2, 1, 'Toshkent Aeroport', 'Aeroport yoli', 'Toshkent', 'Toshkent', '100167', 'Ozbekiston'),
(3, 2, 'Samarqand Vokzal', 'Beruniy kochasi, 5', 'Samarqand', 'Samarqand', '140100', 'Ozbekiston'),
(4, 2, 'Buxoro Eski Shahar', 'B. Naqshband kochasi', 'Buxoro', 'Buxoro', '200100', 'Ozbekiston');

INSERT INTO parking_stalls (id, location_id, stall_number, location_identifier) VALUES
(1, 1, 'T-01', 'Qavat 1'),
(2, 1, 'T-02', 'Qavat 1'),
(3, 2, 'A-10', 'Terminal 2'),
(4, 2, 'A-11', 'Terminal 2'),
(5, 3, 'S-01', 'Asosiy Toxtash Joyi'),
(6, 3, 'S-02', 'Asosiy Toxtash Joyi'),
(7, 4, 'B-01', 'Mehmonxona Oldi'),
(8, 4, 'B-02', 'Mehmonxona Oldi'),
(9, 1, 'T-03', 'Qavat 2'),
(10, 2, 'A-12', 'Terminal 2');

INSERT INTO barcodes (id, barcode, issued_at, active) VALUES
(1, 'BC-NEX-001', '2026-05-01 07:48:37', 1),
(2, 'BC-COB-001', '2026-05-01 07:48:37', 1),
(3, 'BC-GEN-001', '2026-05-01 07:48:37', 1),
(4, 'BC-MAL-001', '2026-05-01 07:48:37', 1),
(5, 'BC-TRA-001', '2026-05-01 07:48:37', 1),
(6, 'BC-TBL-001', '2026-05-01 07:48:37', 1),
(7, 'BC-SPA-001', '2026-05-01 07:48:37', 1),
(8, 'BC-DAM-001', '2026-05-01 07:48:37', 1),
(9, 'BC-TAH-001', '2026-05-01 07:48:37', 1),
(10, 'BC-CAP-001', '2026-05-01 07:48:37', 1);

INSERT INTO vehicles (
    id, location_id, parking_stall_id, barcode_id, vehicle_type, car_type, license_number,
    stock_number, passenger_capacity, has_sunroof, status, model, make, manufacturing_year,
    mileage, is_active, price_per_day, image_path, transmission_type, fuel_type, fuel_level
) VALUES
(1, 1, 1, 1, 1, 1, '01 A 777 AA', 'V001', 5, 0, 2, 'Nexia 3', 'Chevrolet', 2022, 15000, 1, 10.00, 'https://crc.uz/wp-content/uploads/2023/02/Nexia-3.png', 2, 2, 100),
(2, 1, 2, 2, 1, 2, '01 B 123 BB', 'V002', 5, 0, 2, 'Cobalt', 'Chevrolet', 2023, 5000, 1, 10.00, 'https://uzavtocdn.org/photos%2Fd7f2388f6a949a251de6ae72944029699158aa3a62bf86337cdbf6221f962f19.png', 2, 1, 100),
(3, 2, 3, 3, 1, 3, '01 C 456 CC', 'V003', 5, 1, 1, 'Gentra', 'Chevrolet', 2022, 12000, 1, 10.00, 'https://www.chevrolet.uz/assets/images/lacetti/colors/2.png', 1, 1, 100),
(4, 2, 4, 4, 1, 6, '01 M 001 MM', 'V004', 5, 1, 1, 'Malibu 2', 'Chevrolet', 2023, 2000, 1, 10.00, 'https://inrent.uz/uploads/cars/malibu2.png', 1, 1, 100),
(5, 3, 5, 5, 3, 4, '10 X 500 XX', 'V005', 5, 1, 1, 'Tracker 2', 'Chevrolet', 2023, 3000, 1, 10.00, 'https://www.navoiyatx.uz/uploads/car_colors/1633511505.png', 1, 1, 100),
(6, 3, 6, 6, 3, 7, '10 Z 999 ZZ', 'V006', 7, 1, 1, 'Trailblazer', 'Chevrolet', 2021, 45000, 1, 10.00, 'https://stimg.cardekho.com/images/carexteriorimages/930x620/Chevrolet/Chevrolet-Trailblazer/1775/1560511456139/front-left-side-47.jpg', 1, 1, 100),
(7, 4, 7, 7, 1, 1, '80 S 100 SS', 'V007', 4, 0, 1, 'Spark', 'Chevrolet', 2020, 60000, 1, 10.00, 'https://yuz.uz/imageproxy/980x/https://yuz.uz/file/news/b1ff20004ad7014e2b5a18d0031f5ce8.jpg', 2, 2, 100),
(8, 4, 8, 8, 4, 4, '80 D 200 DD', 'V008', 7, 0, 1, 'Damas', 'Chevrolet', 2022, 25000, 1, 10.00, 'https://stat.uz/images/damaswhite.jpg', 2, 2, 100),
(9, 1, 9, 9, 3, 7, '01 T 888 TT', 'V009', 7, 1, 3, 'Tahoe', 'Chevrolet', 2023, 1000, 1, 10.00, 'https://www.vhv.rs/dpng/d/80-805591_chevrolet-tahoe-hd-png-download.png', 1, 1, 100),
(10, 2, 10, 10, 3, 5, '01 K 333 KK', 'V010', 7, 1, 1, 'Captiva', 'Chevrolet', 2022, 1111, 1, 10.00, 'https://inrent.uz/uploads/cars/captiva3.png', 1, 1, 100);

INSERT INTO persons (id, name, street_address, city, state, zipcode, country, email, phone, birth_date) VALUES
(1, 'Alisher Navoiy', NULL, 'Toshkent', NULL, NULL, 'Ozbekiston', 'admin@uzrental.uz', '+998901234567', '1995-01-01'),
(2, 'Nigora Umidova', NULL, 'Toshkent', NULL, NULL, 'Ozbekiston', 'nigora@uzrental.uz', '+998912345678', '1995-01-01'),
(3, 'Baxtiyor Gulomov', NULL, 'Toshkent', NULL, NULL, 'Ozbekiston', 'baxtiyor@uzrental.uz', '+998934567890', '1995-01-01'),
(4, 'Jasur Alimov', NULL, 'Samarqand', NULL, NULL, 'Ozbekiston', 'jasur@gmail.com', '+998941112233', '1995-01-01'),
(5, 'Malika Karimova', NULL, 'Buxoro', NULL, NULL, 'Ozbekiston', 'malika@mail.ru', '+998971110022', '1995-01-01'),
(6, 'a', NULL, NULL, NULL, NULL, NULL, 'a@a.a', NULL, '1995-01-01');

INSERT INTO accounts (id, person_id, username, password_hash, status, role_type, is_active) VALUES
(1, 1, 'admin', '$2a$10$qAFnPZllJ8baIzeN1olNUuzc/KHOoaQiCDygdsb7JIv5LIzaSODqO', 1, 4, 1),
(2, 2, 'nigora', '$2a$10$qAFnPZllJ8baIzeN1olNUuzc/KHOoaQiCDygdsb7JIv5LIzaSODqO', 1, 2, 1),
(3, 3, 'worker', '$2a$10$qAFnPZllJ8baIzeN1olNUuzc/KHOoaQiCDygdsb7JIv5LIzaSODqO', 1, 3, 1),
(4, 4, 'jama', '$2a$10$qAFnPZllJ8baIzeN1olNUuzc/KHOoaQiCDygdsb7JIv5LIzaSODqO', 1, 1, 1),
(5, 5, 'malika', '$2a$10$qAFnPZllJ8baIzeN1olNUuzc/KHOoaQiCDygdsb7JIv5LIzaSODqO', 1, 1, 1);

INSERT INTO receptionists (id, account_id, date_joined) VALUES
(1, 2, '2026-05-01 07:48:37');

INSERT INTO members (id, account_id, driver_license_number, driver_license_expiry) VALUES
(1, 4, 'UZ-AA1234567', '2029-05-01 07:48:37'),
(2, 5, 'UZ-BB7654321', '2031-05-01 07:48:37');

INSERT INTO vehicle_reservations (
    id, reservation_number, member_id, vehicle_id, creation_date, pickup_date, status,
    due_date, return_date, pickup_location_id, return_location_id, processed_by_account_id
) VALUES
(1, 'RES-20260503-000001', 1, 1, '2026-05-03 09:00:00', '2026-05-05 10:00:00', 3, '2026-05-08 12:00:00', NULL, 1, 1, NULL),
(2, 'RES-20260504-000001', 1, 2, '2026-05-04 08:15:00', '2026-05-04 14:00:00', 3, '2026-05-06 12:00:00', NULL, 1, 2, 2),
(3, 'RES-20260502-000001', 1, 9, '2026-05-02 09:30:00', '2026-05-02 13:00:00', 2, '2026-05-07 12:00:00', NULL, 1, 2, 2),
(4, 'RES-20260501-000001', 2, 4, '2026-05-01 10:00:00', '2026-05-01 12:00:00', 7, '2026-05-03 12:00:00', NULL, 2, 2, 2),
(5, 'RES-20260427-000001', 2, 3, '2026-04-27 08:30:00', '2026-04-27 10:00:00', 4, '2026-04-30 11:00:00', '2026-04-30 15:30:00', 2, 2, 3),
(6, 'RES-20260420-000001', 1, 5, '2026-04-20 09:10:00', '2026-04-20 10:00:00', 4, '2026-04-23 10:00:00', '2026-04-23 09:45:00', 3, 3, 3);

INSERT INTO rental_insurances (id, reservation_id, insurance_type, price) VALUES
(1, 1, 1, 15.00),
(2, 3, 1, 15.00),
(3, 4, 2, 15.00);

INSERT INTO equipment (id, reservation_id, equipment_type, price) VALUES
(1, 1, 1, 10.00),
(2, 4, 1, 10.00);

INSERT INTO services (id, reservation_id, service_type, price) VALUES
(1, 2, 1, 20.00),
(2, 6, 1, 20.00);

INSERT INTO bills (id, reservation_id, total_amount) VALUES
(1, 1, 55.00),
(2, 2, 40.00),
(3, 3, 65.00),
(4, 4, 45.00),
(5, 5, 55.00),
(6, 6, 50.00);

INSERT INTO bill_items (id, bill_id, item_type, amount, service_name) VALUES
(1, 1, 1, 30.00, 'Base Rental Charge (3 days)'),
(2, 1, 2, 15.00, 'Insurance: Additional insurance (deductible)'),
(3, 1, 3, 10.00, 'Equipment: Navigation'),
(4, 2, 1, 20.00, 'Base Rental Charge (2 days)'),
(5, 2, 4, 20.00, 'Service: Roadside Assistance'),
(6, 3, 1, 50.00, 'Base Rental Charge (5 days)'),
(7, 3, 2, 15.00, 'Insurance: Additional insurance (deductible)'),
(8, 4, 1, 20.00, 'Base Rental Charge (2 days)'),
(9, 4, 2, 15.00, 'Insurance: Personal'),
(10, 4, 3, 10.00, 'Equipment: Navigation'),
(11, 5, 1, 30.00, 'Base Rental Charge (3 days)'),
(12, 5, 6, 20.00, 'Damage Fee'),
(13, 5, 7, 5.00, 'Fuel Fee'),
(14, 6, 1, 30.00, 'Base Rental Charge (3 days)'),
(15, 6, 4, 20.00, 'Service: Roadside Assistance');

INSERT INTO payments (id, bill_id, creation_date, amount, status, payment_type, processed_by_account_id) VALUES
(1, 2, '2026-05-04 08:45:00', 40.00, 3, 1, 2),
(2, 3, '2026-05-02 10:00:00', 65.00, 3, 3, 2),
(3, 4, '2026-05-01 10:30:00', 45.00, 3, 1, 2),
(4, 5, '2026-04-27 09:00:00', 30.00, 3, 1, 2),
(5, 5, '2026-04-30 16:05:00', 15.00, 3, 3, 3),
(6, 6, '2026-04-20 09:35:00', 50.00, 9, 1, NULL);

INSERT INTO credit_card_transactions (id, payment_id, name_on_card) VALUES
(1, 1, 'Jasur Alimov'),
(2, 3, 'Malika Karimova'),
(3, 4, 'Malika Karimova'),
(4, 6, 'Jasur Alimov');

INSERT INTO cash_transactions (id, payment_id, cash_tendered) VALUES
(1, 2, 65.00),
(2, 5, 15.00);

INSERT INTO return_inspections (
    id, reservation_id, vehicle_id, worker_account_id, inspection_date, mileage, fuel_level,
    damage_description, damage_fee, fuel_fee, cleaned, maintenance_required, parking_stall_id, notes
) VALUES
(1, 5, 3, 3, '2026-04-30 15:30:00', 12480, 62, 'Front bumper scratch documented during return inspection.', 20.00, 5.00, TRUE, FALSE, 3, 'Customer paid part of the post-inspection balance.'),
(2, 6, 5, 3, '2026-04-23 09:45:00', 3450, 88, 'Good condition.', 0.00, 0.00, TRUE, FALSE, 5, 'No additional charges.');

INSERT INTO notifications (id, reservation_id, notification_type, created_on, content, is_read, address, email) VALUES
(1, 1, 1, '2026-05-03 09:01:00', 'Reservation RES-20260503-000001 confirmed for Chevrolet Nexia 3.', FALSE, NULL, 'jasur@gmail.com'),
(2, 1, 2, '2026-05-04 09:00:00', 'Upcoming reservation reminder for RES-20260503-000001.', FALSE, NULL, 'jasur@gmail.com'),
(3, 1, 4, '2026-05-04 09:05:00', 'Pickup reminder for reservation RES-20260503-000001.', FALSE, NULL, 'jasur@gmail.com'),
(4, 2, 1, '2026-05-04 08:16:00', 'Reservation RES-20260504-000001 confirmed for Chevrolet Cobalt.', FALSE, NULL, 'jasur@gmail.com'),
(5, 2, 8, '2026-05-04 08:45:00', 'Payment received for reservation RES-20260504-000001.', FALSE, NULL, 'jasur@gmail.com'),
(6, 2, 2, '2026-05-04 09:00:00', 'Upcoming reservation reminder for RES-20260504-000001.', FALSE, NULL, 'jasur@gmail.com'),
(7, 2, 4, '2026-05-04 09:05:00', 'Pickup reminder for reservation RES-20260504-000001.', FALSE, NULL, 'jasur@gmail.com'),
(8, 3, 1, '2026-05-02 09:31:00', 'Reservation RES-20260502-000001 confirmed for Chevrolet Tahoe.', TRUE, NULL, 'jasur@gmail.com'),
(9, 3, 8, '2026-05-02 10:00:00', 'Payment received for reservation RES-20260502-000001.', TRUE, NULL, 'jasur@gmail.com'),
(10, 3, 4, '2026-05-02 13:00:00', 'Vehicle picked up. Your rental has started.', FALSE, NULL, 'jasur@gmail.com'),
(11, 4, 12, '2026-05-03 13:15:00', 'Return initiated for RES-20260501-000001. Worker inspection is required.', FALSE, NULL, 'malika@mail.ru'),
(12, 5, 7, '2026-04-30 15:35:00', 'Vehicle returned successfully for RES-20260427-000001.', FALSE, NULL, 'malika@mail.ru'),
(13, 5, 10, '2026-04-30 15:36:00', 'Damage fee of $20.00 has been added to your bill.', FALSE, NULL, 'malika@mail.ru'),
(14, 5, 11, '2026-04-30 15:37:00', 'Fuel fee of $5.00 has been added to your bill.', FALSE, NULL, 'malika@mail.ru'),
(15, 6, 7, '2026-04-23 09:48:00', 'Vehicle returned successfully for RES-20260420-000001.', TRUE, NULL, 'jasur@gmail.com'),
(16, 6, 8, '2026-04-20 09:35:00', 'Payment received for reservation RES-20260420-000001.', TRUE, NULL, 'jasur@gmail.com');

INSERT INTO vehicle_logs (id, vehicle_id, log_type, description, creation_date, account_id) VALUES
(1, 1, 6, 'Reservation RES-20260503-000001 created for 2026-05-05T10:00 to 2026-05-08T12:00.', '2026-05-03 09:01:00', NULL),
(2, 2, 6, 'Reservation RES-20260504-000001 created and paid before pickup.', '2026-05-04 08:45:00', 2),
(3, 9, 6, 'Vehicle picked up for reservation RES-20260502-000001. Bill fully paid.', '2026-05-02 13:00:00', 2),
(4, 4, 6, 'Return initiated for reservation RES-20260501-000001. Waiting for worker inspection.', '2026-05-03 13:15:00', 2),
(5, 3, 3, 'Vehicle returned. Mileage: 12480, Fuel: 62%, Cleaned: true, Maintenance required: false, Damage: Front bumper scratch.', '2026-04-30 15:30:00', 3),
(6, 5, 3, 'Vehicle returned. Mileage: 3450, Fuel: 88%, Cleaned: true, Maintenance required: false, Damage: Good condition.', '2026-04-23 09:45:00', 3);

ALTER TABLE accounts AUTO_INCREMENT = 7;
ALTER TABLE members AUTO_INCREMENT = 4;
ALTER TABLE persons AUTO_INCREMENT = 7;
ALTER TABLE vehicle_reservations AUTO_INCREMENT = 7;
ALTER TABLE additional_drivers AUTO_INCREMENT = 1;
ALTER TABLE rental_insurances AUTO_INCREMENT = 4;
ALTER TABLE equipment AUTO_INCREMENT = 3;
ALTER TABLE services AUTO_INCREMENT = 3;
ALTER TABLE bills AUTO_INCREMENT = 7;
ALTER TABLE bill_items AUTO_INCREMENT = 16;
ALTER TABLE payments AUTO_INCREMENT = 7;
ALTER TABLE credit_card_transactions AUTO_INCREMENT = 5;
ALTER TABLE check_transactions AUTO_INCREMENT = 1;
ALTER TABLE cash_transactions AUTO_INCREMENT = 3;
ALTER TABLE notifications AUTO_INCREMENT = 17;
ALTER TABLE vehicle_logs AUTO_INCREMENT = 7;
ALTER TABLE return_inspections AUTO_INCREMENT = 3;
