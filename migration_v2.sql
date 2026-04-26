-- migration_v2.sql
USE car_rental_system;

-- Track which staff member processed the return
ALTER TABLE vehicle_reservations ADD COLUMN processed_by_account_id INT NULL;
ALTER TABLE vehicle_reservations ADD CONSTRAINT fk_processed_by FOREIGN KEY (processed_by_account_id) REFERENCES accounts(id);

-- Audit log for vehicle changes
ALTER TABLE vehicle_logs ADD COLUMN account_id INT NULL;
ALTER TABLE vehicle_logs ADD CONSTRAINT fk_log_account FOREIGN KEY (account_id) REFERENCES accounts(id);
