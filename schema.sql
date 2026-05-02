-- schema.sql for Car Rental System
CREATE DATABASE IF NOT EXISTS car_rental_system;
USE car_rental_system;

-- 1. car_rental_systems
CREATE TABLE IF NOT EXISTS car_rental_systems (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- 2. locations
CREATE TABLE IF NOT EXISTS locations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    system_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    street_address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    zipcode VARCHAR(30),
    country VARCHAR(100),
    FOREIGN KEY (system_id) REFERENCES car_rental_systems(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. parking_stalls
CREATE TABLE IF NOT EXISTS parking_stalls (
    id INT PRIMARY KEY AUTO_INCREMENT,
    location_id INT NOT NULL,
    stall_number VARCHAR(50) NOT NULL,
    location_identifier VARCHAR(100),
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. barcodes
CREATE TABLE IF NOT EXISTS barcodes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    barcode VARCHAR(100) UNIQUE NOT NULL,
    issued_at DATETIME NOT NULL,
    active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- 5. vehicles
CREATE TABLE IF NOT EXISTS vehicles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    location_id INT NOT NULL,
    parking_stall_id INT UNIQUE NULL,
    barcode_id INT UNIQUE NOT NULL,
    vehicle_type INT NOT NULL,
    car_type INT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    stock_number VARCHAR(50) UNIQUE NOT NULL,
    passenger_capacity INT,
    has_sunroof BOOLEAN DEFAULT FALSE,
    status INT NOT NULL,
    model VARCHAR(100),
    make VARCHAR(100),
    manufacturing_year INT,
    mileage INT,
    is_active BOOLEAN DEFAULT TRUE,
    price_per_day DECIMAL(10,2) DEFAULT 0.00,
    image_path VARCHAR(500) NULL,
    transmission_type INT DEFAULT 1,
    fuel_type INT DEFAULT 1,
    fuel_level INT DEFAULT 100,
    FOREIGN KEY (location_id) REFERENCES locations(id),
    FOREIGN KEY (parking_stall_id) REFERENCES parking_stalls(id),
    FOREIGN KEY (barcode_id) REFERENCES barcodes(id)
) ENGINE=InnoDB;

-- 6. persons
CREATE TABLE IF NOT EXISTS persons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    street_address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    zipcode VARCHAR(30),
    country VARCHAR(100),
    email VARCHAR(150),
    phone VARCHAR(50),
    birth_date DATE NULL
) ENGINE=InnoDB;

-- 7. accounts
CREATE TABLE IF NOT EXISTS accounts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    person_id INT UNIQUE NOT NULL,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status INT NOT NULL,
    role_type INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. vehicle_logs
CREATE TABLE IF NOT EXISTS vehicle_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    vehicle_id INT NOT NULL,
    log_type INT NOT NULL,
    description TEXT,
    creation_date DATETIME NOT NULL,
    account_id INT NULL,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE,
FOREIGN KEY (account_id) REFERENCES accounts(id)
) ENGINE=InnoDB;

-- 9. members
CREATE TABLE IF NOT EXISTS members (
    id INT PRIMARY KEY AUTO_INCREMENT,
    account_id INT UNIQUE NOT NULL,
    driver_license_number VARCHAR(100) UNIQUE NOT NULL,
    driver_license_expiry DATETIME NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 10. receptionists
CREATE TABLE IF NOT EXISTS receptionists (
    id INT PRIMARY KEY AUTO_INCREMENT,
    account_id INT UNIQUE NOT NULL,
    date_joined DATETIME NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 11. barcode_readers
CREATE TABLE IF NOT EXISTS barcode_readers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    registered_at DATETIME NOT NULL,
    active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- 12. vehicle_reservations
CREATE TABLE IF NOT EXISTS vehicle_reservations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_number VARCHAR(100) UNIQUE NOT NULL,
    member_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    creation_date DATETIME NOT NULL,
    pickup_date DATETIME NOT NULL,
    status INT NOT NULL,
    due_date DATETIME NOT NULL,
    return_date DATETIME NULL,
    pickup_location_id INT NOT NULL,
    return_location_id INT NOT NULL,
    processed_by_account_id INT NULL,
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    FOREIGN KEY (pickup_location_id) REFERENCES locations(id),
    FOREIGN KEY (return_location_id) REFERENCES locations(id),
    FOREIGN KEY (processed_by_account_id) REFERENCES accounts(id)
) ENGINE=InnoDB;

-- 24. return_inspections
CREATE TABLE IF NOT EXISTS return_inspections (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    worker_account_id INT NOT NULL,
    inspection_date DATETIME NOT NULL,
    mileage INT NOT NULL,
    fuel_level INT NOT NULL,
    damage_description TEXT NULL,
    damage_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    fuel_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    cleaned BOOLEAN DEFAULT FALSE,
    maintenance_required BOOLEAN DEFAULT FALSE,
    parking_stall_id INT NULL,
    notes TEXT NULL,
    FOREIGN KEY (reservation_id) REFERENCES vehicle_reservations(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    FOREIGN KEY (worker_account_id) REFERENCES accounts(id),
    FOREIGN KEY (parking_stall_id) REFERENCES parking_stalls(id)
) ENGINE=InnoDB;

-- 13. additional_drivers
CREATE TABLE IF NOT EXISTS additional_drivers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL,
    person_id INT NOT NULL,
    driver_id VARCHAR(100),
    FOREIGN KEY (reservation_id) REFERENCES vehicle_reservations(id) ON DELETE CASCADE,
    FOREIGN KEY (person_id) REFERENCES persons(id)
) ENGINE=InnoDB;

-- 14. rental_insurances
CREATE TABLE IF NOT EXISTS rental_insurances (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL,
    insurance_type INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES vehicle_reservations(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 15. equipment
CREATE TABLE IF NOT EXISTS equipment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL,
    equipment_type INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES vehicle_reservations(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 16. services
CREATE TABLE IF NOT EXISTS services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL,
    service_type INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES vehicle_reservations(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 17. bills
CREATE TABLE IF NOT EXISTS bills (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT UNIQUE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (reservation_id) REFERENCES vehicle_reservations(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 18. bill_items
CREATE TABLE IF NOT EXISTS bill_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL,
    item_type INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    service_name VARCHAR(100),
    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 19. payments
CREATE TABLE IF NOT EXISTS payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL,
    creation_date DATETIME NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status INT NOT NULL,
    payment_type INT NOT NULL,
    processed_by_account_id INT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by_account_id) REFERENCES accounts(id)
) ENGINE=InnoDB;

-- 20. credit_card_transactions
CREATE TABLE IF NOT EXISTS credit_card_transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    payment_id INT UNIQUE NOT NULL,
    name_on_card VARCHAR(150),
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 21. check_transactions
CREATE TABLE IF NOT EXISTS check_transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    payment_id INT UNIQUE NOT NULL,
    bank_name VARCHAR(150),
    check_number VARCHAR(100),
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 22. cash_transactions
CREATE TABLE IF NOT EXISTS cash_transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    payment_id INT UNIQUE NOT NULL,
    cash_tendered DECIMAL(10,2),
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 23. notifications
CREATE TABLE IF NOT EXISTS notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL,
    notification_type INT NOT NULL,
    created_on DATETIME NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    address VARCHAR(255) NULL,
    email VARCHAR(150) NULL,
    FOREIGN KEY (reservation_id) REFERENCES vehicle_reservations(id) ON DELETE CASCADE
) ENGINE=InnoDB;

