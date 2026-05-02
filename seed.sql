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

ALTER TABLE accounts AUTO_INCREMENT = 7;
ALTER TABLE members AUTO_INCREMENT = 4;
ALTER TABLE persons AUTO_INCREMENT = 7;
ALTER TABLE vehicle_reservations AUTO_INCREMENT = 1;
ALTER TABLE bills AUTO_INCREMENT = 1;
ALTER TABLE bill_items AUTO_INCREMENT = 1;
ALTER TABLE payments AUTO_INCREMENT = 1;
ALTER TABLE notifications AUTO_INCREMENT = 1;
ALTER TABLE vehicle_logs AUTO_INCREMENT = 1;
ALTER TABLE return_inspections AUTO_INCREMENT = 1;
