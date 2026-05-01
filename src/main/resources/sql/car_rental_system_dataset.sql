CREATE DATABASE IF NOT EXISTS car_rental_system;
USE car_rental_system;

-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: mysql-8.0
-- Generation Time: May 01, 2026 at 02:56 AM
-- Server version: 8.0.41
-- PHP Version: 8.1.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `car_rental_system`
--

-- --------------------------------------------------------

--
-- Table structure for table `accounts`
--

CREATE TABLE `accounts` (
  `id` int NOT NULL,
  `person_id` int NOT NULL,
  `username` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `status` int NOT NULL,
  `role_type` int NOT NULL,
  `is_active` tinyint(1) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `accounts`
--

INSERT INTO `accounts` (`id`, `person_id`, `username`, `password_hash`, `status`, `role_type`, `is_active`) VALUES
(1, 1, 'admin', 'password123', 1, 4, 1),
(2, 2, 'nigora', 'password123', 1, 2, 1),
(3, 3, 'worker', 'password123', 1, 3, 1),
(4, 4, 'jama', 'pass123', 1, 1, 1),
(5, 5, 'malika', 'password123', 1, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `additional_drivers`
--

CREATE TABLE `additional_drivers` (
  `id` int NOT NULL,
  `reservation_id` int NOT NULL,
  `person_id` int NOT NULL,
  `driver_id` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `barcodes`
--

CREATE TABLE `barcodes` (
  `id` int NOT NULL,
  `barcode` varchar(100) NOT NULL,
  `issued_at` timestamp NOT NULL,
  `active` tinyint(1) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `barcodes`
--

INSERT INTO `barcodes` (`id`, `barcode`, `issued_at`, `active`) VALUES
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

-- --------------------------------------------------------

--
-- Table structure for table `barcode_readers`
--

CREATE TABLE `barcode_readers` (
  `id` int NOT NULL,
  `registered_at` timestamp NOT NULL,
  `active` tinyint(1) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `bills`
--

CREATE TABLE `bills` (
  `id` int NOT NULL,
  `reservation_id` int NOT NULL,
  `total_amount` decimal(10,2) NOT NULL DEFAULT '0.00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `bills`
--

INSERT INTO `bills` (`id`, `reservation_id`, `total_amount`) VALUES
(1, 1, 0.00),
(2, 2, 0.00),
(3, 3, 0.00),
(4, 4, 100.00);

-- --------------------------------------------------------

--
-- Table structure for table `bill_items`
--

CREATE TABLE `bill_items` (
  `id` int NOT NULL,
  `bill_id` int NOT NULL,
  `item_type` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `service_name` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `bill_items`
--

INSERT INTO `bill_items` (`id`, `bill_id`, `item_type`, `amount`, `service_name`) VALUES
(1, 1, 1, 0.00, 'Base Rental Charge (28 days)'),
(2, 2, 1, 0.00, 'Base Rental Charge (21 days)'),
(3, 3, 1, 0.00, 'Base Rental Charge (29 days)'),
(4, 4, 1, 60.00, 'Base Rental Charge (6 days)'),
(5, 4, 4, 10.00, 'Equipment: NAVIGATION'),
(6, 4, 3, 30.00, 'Damage/Other Assessment Fine');

-- --------------------------------------------------------

--
-- Table structure for table `car_rental_systems`
--

CREATE TABLE `car_rental_systems` (
  `id` int NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `car_rental_systems`
--

INSERT INTO `car_rental_systems` (`id`, `name`) VALUES
(1, 'UzAuto Rental'),
(2, 'Tashkent City Cars');

-- --------------------------------------------------------

--
-- Table structure for table `cash_transactions`
--

CREATE TABLE `cash_transactions` (
  `id` int NOT NULL,
  `payment_id` int NOT NULL,
  `cash_tendered` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `cash_transactions`
--

INSERT INTO `cash_transactions` (`id`, `payment_id`, `cash_tendered`) VALUES
(1, 1, 70.00);

-- --------------------------------------------------------

--
-- Table structure for table `check_transactions`
--

CREATE TABLE `check_transactions` (
  `id` int NOT NULL,
  `payment_id` int NOT NULL,
  `bank_name` varchar(150) DEFAULT NULL,
  `check_number` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `credit_card_transactions`
--

CREATE TABLE `credit_card_transactions` (
  `id` int NOT NULL,
  `payment_id` int NOT NULL,
  `name_on_card` varchar(150) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `equipment`
--

CREATE TABLE `equipment` (
  `id` int NOT NULL,
  `reservation_id` int NOT NULL,
  `equipment_type` int NOT NULL,
  `price` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `equipment`
--

INSERT INTO `equipment` (`id`, `reservation_id`, `equipment_type`, `price`) VALUES
(1, 4, 1, 10.00);

-- --------------------------------------------------------

--
-- Table structure for table `locations`
--

CREATE TABLE `locations` (
  `id` int NOT NULL,
  `system_id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `street_address` varchar(255) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `state` varchar(100) DEFAULT NULL,
  `zipcode` varchar(30) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `locations`
--

INSERT INTO `locations` (`id`, `system_id`, `name`, `street_address`, `city`, `state`, `zipcode`, `country`) VALUES
(1, 1, 'Toshkent Markaz', 'Amir Temur ko\'chasi, 10', 'Toshkent', 'Toshkent', '100000', 'O\'zbekiston'),
(2, 1, 'Toshkent Aeroport', 'Aeroport yo\'li', 'Toshkent', 'Toshkent', '100167', 'O\'zbekiston'),
(3, 2, 'Samarqand Vokzal', 'Beruniy ko\'chasi, 5', 'Samarqand', 'Samarqand', '140100', 'O\'zbekiston'),
(4, 2, 'Buxoro Eski Shahar', 'B. Naqshband ko\'chasi', 'Buxoro', 'Buxoro', '200100', 'O\'zbekiston');

-- --------------------------------------------------------

--
-- Table structure for table `members`
--

CREATE TABLE `members` (
  `id` int NOT NULL,
  `account_id` int NOT NULL,
  `driver_license_number` varchar(100) NOT NULL,
  `driver_license_expiry` timestamp NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `members`
--

INSERT INTO `members` (`id`, `account_id`, `driver_license_number`, `driver_license_expiry`) VALUES
(1, 4, 'UZ-AA1234567', '2029-05-01 07:48:37'),
(2, 5, 'UZ-BB7654321', '2031-05-01 07:48:37');

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` int NOT NULL,
  `reservation_id` int NOT NULL,
  `notification_type` int NOT NULL,
  `created_on` timestamp NOT NULL,
  `content` text NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`id`, `reservation_id`, `notification_type`, `created_on`, `content`, `address`, `email`) VALUES
(1, 1, 3, '2026-05-01 15:57:35', 'Reservation RES-20260501-0001-737 pending for Chevrolet Nexia 3', NULL, NULL),
(2, 2, 3, '2026-05-01 18:21:08', 'Reservation RES-20260501-0002-776 pending for Chevrolet Cobalt', NULL, NULL),
(3, 3, 3, '2026-05-01 18:52:17', 'Reservation RES-20260501-000003 confirmed for Chevrolet Tahoe', NULL, NULL),
(4, 3, 3, '2026-05-01 18:52:48', 'Vehicle picked up. Your rental has started!', NULL, NULL),
(5, 4, 3, '2026-05-01 18:54:04', 'Reservation RES-20260501-000004 confirmed for Chevrolet Captiva', NULL, NULL),
(6, 4, 3, '2026-05-01 18:54:37', 'Vehicle picked up. Your rental has started!', NULL, NULL),
(7, 4, 3, '2026-05-01 18:54:58', 'A fine of $30 has been added to your bill for damage/other issues.', NULL, NULL),
(8, 4, 3, '2026-05-01 18:54:58', 'Vehicle returned successfully. Mileage updated to 1111. Thank you!', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `parking_stalls`
--

CREATE TABLE `parking_stalls` (
  `id` int NOT NULL,
  `location_id` int NOT NULL,
  `stall_number` varchar(50) NOT NULL,
  `location_identifier` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `parking_stalls`
--

INSERT INTO `parking_stalls` (`id`, `location_id`, `stall_number`, `location_identifier`) VALUES
(1, 1, 'T-01', 'Qavat 1'),
(2, 1, 'T-02', 'Qavat 1'),
(3, 2, 'A-10', 'Terminal 2'),
(4, 2, 'A-11', 'Terminal 2'),
(5, 3, 'S-01', 'Asosiy To\'xtash Joyi'),
(6, 3, 'S-02', 'Asosiy To\'xtash Joyi'),
(7, 4, 'B-01', 'Mehmonxona Oldi'),
(8, 4, 'B-02', 'Mehmonxona Oldi'),
(9, 1, 'T-03', 'Qavat 2'),
(10, 2, 'A-12', 'Terminal 2');

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `id` int NOT NULL,
  `bill_id` int NOT NULL,
  `creation_date` timestamp NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `status` int NOT NULL,
  `payment_type` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`id`, `bill_id`, `creation_date`, `amount`, `status`, `payment_type`) VALUES
(1, 4, '2026-05-01 18:54:33', 70.00, 3, 3);

-- --------------------------------------------------------

--
-- Table structure for table `persons`
--

CREATE TABLE `persons` (
  `id` int NOT NULL,
  `name` varchar(150) NOT NULL,
  `street_address` varchar(255) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `state` varchar(100) DEFAULT NULL,
  `zipcode` varchar(30) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `persons`
--

INSERT INTO `persons` (`id`, `name`, `street_address`, `city`, `state`, `zipcode`, `country`, `email`, `phone`) VALUES
(1, 'Alisher Navoiy', NULL, 'Toshkent', NULL, NULL, 'O\'zbekiston', 'admin@uzrental.uz', '+998901234567'),
(2, 'Nigora Umidova', NULL, 'Toshkent', NULL, NULL, 'O\'zbekiston', 'nigora@uzrental.uz', '+998912345678'),
(3, 'Baxtiyor G\'ulomov', NULL, 'Toshkent', NULL, NULL, 'O\'zbekiston', 'baxtiyor@uzrental.uz', '+998934567890'),
(4, 'Jasur Alimov', NULL, 'Samarqand', NULL, NULL, 'O\'zbekiston', 'jasur@gmail.com', '+998941112233'),
(5, 'Malika Karimova', NULL, 'Buxoro', NULL, NULL, 'O\'zbekiston', 'malika@mail.ru', '+998971110022'),
(6, 'a', NULL, NULL, NULL, NULL, NULL, 'a@a.a', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `receptionists`
--

CREATE TABLE `receptionists` (
  `id` int NOT NULL,
  `account_id` int NOT NULL,
  `date_joined` timestamp NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `receptionists`
--

INSERT INTO `receptionists` (`id`, `account_id`, `date_joined`) VALUES
(1, 2, '2026-05-01 07:48:37');

-- --------------------------------------------------------

--
-- Table structure for table `rental_insurances`
--

CREATE TABLE `rental_insurances` (
  `id` int NOT NULL,
  `reservation_id` int NOT NULL,
  `insurance_type` int NOT NULL,
  `price` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `services`
--

CREATE TABLE `services` (
  `id` int NOT NULL,
  `reservation_id` int NOT NULL,
  `service_type` int NOT NULL,
  `price` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `vehicles`
--

CREATE TABLE `vehicles` (
  `id` int NOT NULL,
  `location_id` int NOT NULL,
  `parking_stall_id` int DEFAULT NULL,
  `barcode_id` int NOT NULL,
  `vehicle_type` int NOT NULL,
  `car_type` int DEFAULT NULL,
  `license_number` varchar(50) NOT NULL,
  `stock_number` varchar(50) NOT NULL,
  `passenger_capacity` int DEFAULT NULL,
  `has_sunroof` tinyint(1) DEFAULT '0',
  `status` int NOT NULL,
  `model` varchar(100) DEFAULT NULL,
  `make` varchar(100) DEFAULT NULL,
  `manufacturing_year` int DEFAULT NULL,
  `mileage` int DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `price_per_day` decimal(10,2) DEFAULT '0.00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `vehicles`
--

INSERT INTO `vehicles` (`id`, `location_id`, `parking_stall_id`, `barcode_id`, `vehicle_type`, `car_type`, `license_number`, `stock_number`, `passenger_capacity`, `has_sunroof`, `status`, `model`, `make`, `manufacturing_year`, `mileage`, `is_active`, `price_per_day`) VALUES
(1, 1, 1, 1, 1, 1, '01 A 777 AA', 'V001', 5, 0, 2, 'Nexia 3', 'Chevrolet', 2022, 15000, 1, 10.00),
(2, 1, 2, 2, 1, 2, '01 B 123 BB', 'V002', 5, 0, 2, 'Cobalt', 'Chevrolet', 2023, 5000, 1, 10.00),
(3, 2, 3, 3, 1, 3, '01 C 456 CC', 'V003', 5, 1, 1, 'Gentra', 'Chevrolet', 2022, 12000, 1, 10.00),
(4, 2, 4, 4, 1, 6, '01 M 001 MM', 'V004', 5, 1, 1, 'Malibu 2', 'Chevrolet', 2023, 2000, 1, 10.00),
(5, 3, 5, 5, 3, 4, '10 X 500 XX', 'V005', 5, 1, 1, 'Tracker 2', 'Chevrolet', 2023, 3000, 1, 10.00),
(6, 3, 6, 6, 3, 7, '10 Z 999 ZZ', 'V006', 7, 1, 1, 'Trailblazer', 'Chevrolet', 2021, 45000, 1, 10.00),
(7, 4, 7, 7, 1, 1, '80 S 100 SS', 'V007', 4, 0, 1, 'Spark', 'Chevrolet', 2020, 60000, 1, 10.00),
(8, 4, 8, 8, 4, 4, '80 D 200 DD', 'V008', 7, 0, 1, 'Damas', 'Chevrolet', 2022, 25000, 1, 10.00),
(9, 1, 9, 9, 3, 7, '01 T 888 TT', 'V009', 7, 1, 3, 'Tahoe', 'Chevrolet', 2023, 1000, 1, 10.00),
(10, 2, 10, 10, 3, 5, '01 K 333 KK', 'V010', 7, 1, 1, 'Captiva', 'Chevrolet', 2022, 1111, 1, 10.00);

-- --------------------------------------------------------

--
-- Table structure for table `vehicle_logs`
--

CREATE TABLE `vehicle_logs` (
  `id` int NOT NULL,
  `vehicle_id` int NOT NULL,
  `log_type` int NOT NULL,
  `description` text,
  `creation_date` timestamp NOT NULL,
  `account_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `vehicle_logs`
--

INSERT INTO `vehicle_logs` (`id`, `vehicle_id`, `log_type`, `description`, `creation_date`, `account_id`) VALUES
(1, 9, 6, 'Vehicle picked up for reservation RES-20260501-000003. Bill prepared.', '2026-05-01 18:52:48', 4),
(2, 10, 6, 'Vehicle picked up for reservation RES-20260501-000004. Bill prepared.', '2026-05-01 18:54:37', 4),
(3, 10, 3, 'Vehicle returned. Condition: Good', '2026-05-01 18:54:58', 4);

-- --------------------------------------------------------

--
-- Table structure for table `vehicle_reservations`
--

CREATE TABLE `vehicle_reservations` (
  `id` int NOT NULL,
  `reservation_number` varchar(100) NOT NULL,
  `member_id` int NOT NULL,
  `vehicle_id` int NOT NULL,
  `creation_date` timestamp NOT NULL,
  `status` int NOT NULL,
  `due_date` timestamp NOT NULL,
  `return_date` timestamp NULL DEFAULT NULL,
  `pickup_location_id` int NOT NULL,
  `return_location_id` int NOT NULL,
  `processed_by_account_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `vehicle_reservations`
--

INSERT INTO `vehicle_reservations` (`id`, `reservation_number`, `member_id`, `vehicle_id`, `creation_date`, `status`, `due_date`, `return_date`, `pickup_location_id`, `return_location_id`, `processed_by_account_id`) VALUES
(1, 'RES-20260501-0001-737', 1, 1, '2026-05-01 15:57:35', 2, '2026-05-30 03:00:00', NULL, 1, 2, NULL),
(2, 'RES-20260501-0002-776', 1, 2, '2026-05-01 18:21:08', 2, '2026-05-23 03:00:00', NULL, 1, 1, NULL),
(3, 'RES-20260501-000003', 1, 9, '2026-05-01 18:52:17', 2, '2026-05-31 03:00:00', NULL, 1, 2, NULL),
(4, 'RES-20260501-000004', 1, 10, '2026-05-01 18:54:04', 4, '2026-05-08 03:00:00', '2026-05-01 18:54:58', 2, 1, 4);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `person_id` (`person_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `additional_drivers`
--
ALTER TABLE `additional_drivers`
  ADD PRIMARY KEY (`id`),
  ADD KEY `reservation_id` (`reservation_id`),
  ADD KEY `person_id` (`person_id`);

--
-- Indexes for table `barcodes`
--
ALTER TABLE `barcodes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `barcode` (`barcode`);

--
-- Indexes for table `barcode_readers`
--
ALTER TABLE `barcode_readers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `bills`
--
ALTER TABLE `bills`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `reservation_id` (`reservation_id`);

--
-- Indexes for table `bill_items`
--
ALTER TABLE `bill_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bill_id` (`bill_id`);

--
-- Indexes for table `car_rental_systems`
--
ALTER TABLE `car_rental_systems`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `cash_transactions`
--
ALTER TABLE `cash_transactions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `payment_id` (`payment_id`);

--
-- Indexes for table `check_transactions`
--
ALTER TABLE `check_transactions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `payment_id` (`payment_id`);

--
-- Indexes for table `credit_card_transactions`
--
ALTER TABLE `credit_card_transactions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `payment_id` (`payment_id`);

--
-- Indexes for table `equipment`
--
ALTER TABLE `equipment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `reservation_id` (`reservation_id`);

--
-- Indexes for table `locations`
--
ALTER TABLE `locations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `system_id` (`system_id`);

--
-- Indexes for table `members`
--
ALTER TABLE `members`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `account_id` (`account_id`),
  ADD UNIQUE KEY `driver_license_number` (`driver_license_number`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `reservation_id` (`reservation_id`);

--
-- Indexes for table `parking_stalls`
--
ALTER TABLE `parking_stalls`
  ADD PRIMARY KEY (`id`),
  ADD KEY `location_id` (`location_id`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bill_id` (`bill_id`);

--
-- Indexes for table `persons`
--
ALTER TABLE `persons`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `receptionists`
--
ALTER TABLE `receptionists`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `account_id` (`account_id`);

--
-- Indexes for table `rental_insurances`
--
ALTER TABLE `rental_insurances`
  ADD PRIMARY KEY (`id`),
  ADD KEY `reservation_id` (`reservation_id`);

--
-- Indexes for table `services`
--
ALTER TABLE `services`
  ADD PRIMARY KEY (`id`),
  ADD KEY `reservation_id` (`reservation_id`);

--
-- Indexes for table `vehicles`
--
ALTER TABLE `vehicles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `barcode_id` (`barcode_id`),
  ADD UNIQUE KEY `license_number` (`license_number`),
  ADD UNIQUE KEY `stock_number` (`stock_number`),
  ADD UNIQUE KEY `parking_stall_id` (`parking_stall_id`),
  ADD KEY `location_id` (`location_id`);

--
-- Indexes for table `vehicle_logs`
--
ALTER TABLE `vehicle_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `vehicle_id` (`vehicle_id`),
  ADD KEY `account_id` (`account_id`);

--
-- Indexes for table `vehicle_reservations`
--
ALTER TABLE `vehicle_reservations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `reservation_number` (`reservation_number`),
  ADD KEY `member_id` (`member_id`),
  ADD KEY `vehicle_id` (`vehicle_id`),
  ADD KEY `pickup_location_id` (`pickup_location_id`),
  ADD KEY `return_location_id` (`return_location_id`),
  ADD KEY `processed_by_account_id` (`processed_by_account_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `accounts`
--
ALTER TABLE `accounts`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `additional_drivers`
--
ALTER TABLE `additional_drivers`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `barcodes`
--
ALTER TABLE `barcodes`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `barcode_readers`
--
ALTER TABLE `barcode_readers`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `bills`
--
ALTER TABLE `bills`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `bill_items`
--
ALTER TABLE `bill_items`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `car_rental_systems`
--
ALTER TABLE `car_rental_systems`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `cash_transactions`
--
ALTER TABLE `cash_transactions`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `check_transactions`
--
ALTER TABLE `check_transactions`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `credit_card_transactions`
--
ALTER TABLE `credit_card_transactions`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `equipment`
--
ALTER TABLE `equipment`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `locations`
--
ALTER TABLE `locations`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `members`
--
ALTER TABLE `members`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `parking_stalls`
--
ALTER TABLE `parking_stalls`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `persons`
--
ALTER TABLE `persons`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `receptionists`
--
ALTER TABLE `receptionists`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `rental_insurances`
--
ALTER TABLE `rental_insurances`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `services`
--
ALTER TABLE `services`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `vehicles`
--
ALTER TABLE `vehicles`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `vehicle_logs`
--
ALTER TABLE `vehicle_logs`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `vehicle_reservations`
--
ALTER TABLE `vehicle_reservations`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `accounts`
--
ALTER TABLE `accounts`
  ADD CONSTRAINT `accounts_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `persons` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `additional_drivers`
--
ALTER TABLE `additional_drivers`
  ADD CONSTRAINT `additional_drivers_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `vehicle_reservations` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `additional_drivers_ibfk_2` FOREIGN KEY (`person_id`) REFERENCES `persons` (`id`);

--
-- Constraints for table `bills`
--
ALTER TABLE `bills`
  ADD CONSTRAINT `bills_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `vehicle_reservations` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `bill_items`
--
ALTER TABLE `bill_items`
  ADD CONSTRAINT `bill_items_ibfk_1` FOREIGN KEY (`bill_id`) REFERENCES `bills` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `cash_transactions`
--
ALTER TABLE `cash_transactions`
  ADD CONSTRAINT `cash_transactions_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `check_transactions`
--
ALTER TABLE `check_transactions`
  ADD CONSTRAINT `check_transactions_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `credit_card_transactions`
--
ALTER TABLE `credit_card_transactions`
  ADD CONSTRAINT `credit_card_transactions_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `equipment`
--
ALTER TABLE `equipment`
  ADD CONSTRAINT `equipment_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `vehicle_reservations` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `locations`
--
ALTER TABLE `locations`
  ADD CONSTRAINT `locations_ibfk_1` FOREIGN KEY (`system_id`) REFERENCES `car_rental_systems` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `members`
--
ALTER TABLE `members`
  ADD CONSTRAINT `members_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `vehicle_reservations` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `parking_stalls`
--
ALTER TABLE `parking_stalls`
  ADD CONSTRAINT `parking_stalls_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`bill_id`) REFERENCES `bills` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `receptionists`
--
ALTER TABLE `receptionists`
  ADD CONSTRAINT `receptionists_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `rental_insurances`
--
ALTER TABLE `rental_insurances`
  ADD CONSTRAINT `rental_insurances_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `vehicle_reservations` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `services`
--
ALTER TABLE `services`
  ADD CONSTRAINT `services_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `vehicle_reservations` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `vehicles`
--
ALTER TABLE `vehicles`
  ADD CONSTRAINT `vehicles_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`),
  ADD CONSTRAINT `vehicles_ibfk_2` FOREIGN KEY (`parking_stall_id`) REFERENCES `parking_stalls` (`id`),
  ADD CONSTRAINT `vehicles_ibfk_3` FOREIGN KEY (`barcode_id`) REFERENCES `barcodes` (`id`);

--
-- Constraints for table `vehicle_logs`
--
ALTER TABLE `vehicle_logs`
  ADD CONSTRAINT `vehicle_logs_ibfk_1` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `vehicle_logs_ibfk_2` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`);

--
-- Constraints for table `vehicle_reservations`
--
ALTER TABLE `vehicle_reservations`
  ADD CONSTRAINT `vehicle_reservations_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  ADD CONSTRAINT `vehicle_reservations_ibfk_2` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`),
  ADD CONSTRAINT `vehicle_reservations_ibfk_3` FOREIGN KEY (`pickup_location_id`) REFERENCES `locations` (`id`),
  ADD CONSTRAINT `vehicle_reservations_ibfk_4` FOREIGN KEY (`return_location_id`) REFERENCES `locations` (`id`),
  ADD CONSTRAINT `vehicle_reservations_ibfk_5` FOREIGN KEY (`processed_by_account_id`) REFERENCES `accounts` (`id`);

ALTER TABLE `vehicles`
  ADD COLUMN IF NOT EXISTS `image_path` varchar(500) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `transmission_type` int DEFAULT '1',
  ADD COLUMN IF NOT EXISTS `fuel_type` int DEFAULT '1',
  ADD COLUMN IF NOT EXISTS `fuel_level` int DEFAULT '100';

ALTER TABLE `persons`
  ADD COLUMN IF NOT EXISTS `birth_date` date DEFAULT NULL;

ALTER TABLE `vehicle_reservations`
  MODIFY `creation_date` datetime NOT NULL,
  MODIFY `due_date` datetime NOT NULL,
  MODIFY `return_date` datetime DEFAULT NULL;

ALTER TABLE `vehicle_reservations`
  ADD COLUMN IF NOT EXISTS `pickup_date` datetime DEFAULT NULL AFTER `creation_date`;

UPDATE `vehicle_reservations` SET `pickup_date` = `creation_date`;

UPDATE `vehicles` SET `image_path` = 'images/chevrolet-nexia-3.jpg', `transmission_type` = 1, `fuel_type` = 1, `fuel_level` = 80 WHERE `id` = 1;
UPDATE `vehicles` SET `image_path` = 'images/chevrolet-cobalt.jpg', `transmission_type` = 1, `fuel_type` = 1, `fuel_level` = 90 WHERE `id` = 2;
UPDATE `vehicles` SET `image_path` = 'images/chevrolet-gentra.jpg', `transmission_type` = 2, `fuel_type` = 1, `fuel_level` = 100 WHERE `id` = 3;
UPDATE `vehicles` SET `image_path` = 'images/chevrolet-malibu.jpg', `transmission_type` = 1, `fuel_type` = 3, `fuel_level` = 70 WHERE `id` = 4;
UPDATE `vehicles` SET `image_path` = 'images/chevrolet-tracker.jpg', `transmission_type` = 1, `fuel_type` = 1, `fuel_level` = 95 WHERE `id` = 5;
UPDATE `vehicles` SET `image_path` = 'images/chevrolet-trailblazer.jpg', `transmission_type` = 1, `fuel_type` = 2, `fuel_level` = 65 WHERE `id` = 6;
UPDATE `vehicles` SET `image_path` = 'images/chevrolet-spark.jpg', `transmission_type` = 2, `fuel_type` = 1, `fuel_level` = 75 WHERE `id` = 7;
UPDATE `vehicles` SET `image_path` = 'images/chevrolet-damas.jpg', `transmission_type` = 2, `fuel_type` = 1, `fuel_level` = 60 WHERE `id` = 8;
UPDATE `vehicles` SET `image_path` = 'images/chevrolet-tahoe.jpg', `transmission_type` = 1, `fuel_type` = 1, `fuel_level` = 85 WHERE `id` = 9;
UPDATE `vehicles` SET `image_path` = 'images/chevrolet-captiva.jpg', `transmission_type` = 1, `fuel_type` = 1, `fuel_level` = 100 WHERE `id` = 10;

UPDATE `persons` SET `birth_date` = '1990-01-01' WHERE `id` IN (1, 2, 3);
UPDATE `persons` SET `birth_date` = '1998-06-15' WHERE `id` = 4;
UPDATE `persons` SET `birth_date` = '1997-08-20' WHERE `id` = 5;

CREATE TABLE IF NOT EXISTS `return_inspections` (
  `id` int NOT NULL AUTO_INCREMENT,
  `reservation_id` int NOT NULL,
  `vehicle_id` int NOT NULL,
  `worker_account_id` int NOT NULL,
  `inspection_date` timestamp NOT NULL,
  `mileage` int NOT NULL,
  `fuel_level` int NOT NULL,
  `damage_description` text,
  `damage_fee` decimal(10,2) NOT NULL DEFAULT '0.00',
  `fuel_fee` decimal(10,2) NOT NULL DEFAULT '0.00',
  `cleaned` tinyint(1) DEFAULT '0',
  `maintenance_required` tinyint(1) DEFAULT '0',
  `parking_stall_id` int DEFAULT NULL,
  `notes` text,
  PRIMARY KEY (`id`),
  KEY `reservation_id` (`reservation_id`),
  KEY `vehicle_id` (`vehicle_id`),
  KEY `worker_account_id` (`worker_account_id`),
  KEY `parking_stall_id` (`parking_stall_id`),
  CONSTRAINT `return_inspections_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `vehicle_reservations` (`id`) ON DELETE CASCADE,
  CONSTRAINT `return_inspections_ibfk_2` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`),
  CONSTRAINT `return_inspections_ibfk_3` FOREIGN KEY (`worker_account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `return_inspections_ibfk_4` FOREIGN KEY (`parking_stall_id`) REFERENCES `parking_stalls` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;


