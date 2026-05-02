package com.cs210.project.services;

import com.cs210.project.constants.Enums.*;
import com.cs210.project.constants.VehicleStatus;
import com.cs210.project.models.Bill;
import com.cs210.project.models.VehicleReservation;
import com.cs210.project.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RentalService {
    private final ReservationRepository resRepo = new ReservationRepository();
    private final VehicleRepository vehicleRepo = new VehicleRepository();
    private final VehicleLogRepository logRepo = new VehicleLogRepository();
    private final BillRepository billRepo = new BillRepository();
    private final NotificationRepository notifyRepo = new NotificationRepository();
    private final PaymentRepository paymentRepo = new PaymentRepository();
    private final ReturnInspectionRepository inspectionRepo = new ReturnInspectionRepository();
    private final CustomerEligibilityService eligibilityService = new CustomerEligibilityService();

    public void pickupVehicle(String reservationNumber, int staffAccountId) throws Exception {
        VehicleReservation res = resRepo.findByNumber(reservationNumber);
        if (res == null) throw new Exception("Reservation not found.");
        if (res.getStatus() != ReservationStatus.CONFIRMED) throw new Exception("Reservation is not in CONFIRMED status.");
        eligibilityService.validateCustomerEligibility(res.getMemberId());
        eligibilityService.validateNoFailedPaymentForReservation(res.getId());

        ensureBillFullyPaid(res);

        // Update statuses only after payment is verified.
        vehicleRepo.updateStatus(res.getVehicleId(), VehicleStatus.LOANED);
        resRepo.updateStatus(res.getId(), ReservationStatus.PENDING); // Mark as active

        logRepo.addLog(res.getVehicleId(), VehicleLogType.OTHER, "Vehicle picked up for reservation " + reservationNumber + ". Bill prepared.", staffAccountId);
        notifyRepo.create(res.getId(), NotificationType.PICKUP_REMINDER, "Vehicle picked up. Your rental has started!");
    }

    public void initiateReturn(String reservationNumber) throws Exception {
        VehicleReservation res = resRepo.findByNumber(reservationNumber);
        if (res == null) throw new Exception("Reservation not found.");
        if (res.getStatus() != ReservationStatus.PENDING) throw new Exception("Vehicle is not currently out for rental.");
        
        resRepo.updateStatus(res.getId(), ReservationStatus.WAITING_FOR_INSPECTION);
        notifyRepo.create(res.getId(), NotificationType.SYSTEM, "Return initiated. Please leave the vehicle at the designated area for inspection.");
    }

    public void returnVehicle(String reservationNumber, int staffAccountId, int newMileage, String conditionLog, BigDecimal manualFine) throws Exception {
        returnVehicle(reservationNumber, staffAccountId, newMileage, 100, conditionLog, manualFine, BigDecimal.ZERO, true, false, null, null);
    }

    public void returnVehicle(String reservationNumber, int staffAccountId, int newMileage, int fuelLevel,
                              String damageDescription, BigDecimal damageFee, BigDecimal fuelFee,
                              boolean cleaned, boolean maintenanceRequired, Integer parkingStallId,
                              String notes) throws Exception {
        VehicleReservation res = resRepo.findByNumber(reservationNumber);
        if (res == null) throw new Exception("Reservation not found.");
        if (res.getStatus() == ReservationStatus.COMPLETED) throw new Exception("Vehicle already returned.");
        if (res.getStatus() != ReservationStatus.PENDING && res.getStatus() != ReservationStatus.WAITING_FOR_INSPECTION) 
            throw new Exception("Vehicle is not in a returnable state.");

        LocalDateTime now = LocalDateTime.now();
        
        // 1. Update reservation with return date and staff info
        String sqlUpdate = "UPDATE vehicle_reservations SET status = ?, return_date = ?, processed_by_account_id = ? WHERE id = ?";
        try (java.sql.Connection conn = com.cs210.project.config.DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
            pstmt.setInt(1, ReservationStatus.COMPLETED.getValue());
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(now));
            pstmt.setInt(3, staffAccountId);
            pstmt.setInt(4, res.getId());
            pstmt.executeUpdate();
        }

        com.cs210.project.models.ReturnInspection inspection = new com.cs210.project.models.ReturnInspection();
        inspection.setReservationId(res.getId());
        inspection.setVehicleId(res.getVehicleId());
        inspection.setWorkerAccountId(staffAccountId);
        inspection.setInspectionDate(now);
        inspection.setMileage(newMileage);
        inspection.setFuelLevel(fuelLevel);
        inspection.setDamageDescription(damageDescription);
        inspection.setDamageFee(damageFee != null ? damageFee : BigDecimal.ZERO);
        inspection.setFuelFee(fuelFee != null ? fuelFee : BigDecimal.ZERO);
        inspection.setCleaned(cleaned);
        inspection.setMaintenanceRequired(maintenanceRequired);
        inspection.setParkingStallId(parkingStallId);
        inspection.setNotes(notes);
        inspectionRepo.create(inspection);

        VehicleStatus finalVehicleStatus = maintenanceRequired || !cleaned ? VehicleStatus.BEING_SERVICED : VehicleStatus.AVAILABLE;
        vehicleRepo.updateReturnState(res.getVehicleId(), newMileage, fuelLevel, parkingStallId, finalVehicleStatus);
        
        // 3. Handle Fines
        Bill bill = billRepo.findByReservationId(res.getId());
        if (bill != null) {
            // Late return fine
            if (now.isAfter(res.getDueDate()) && !billRepo.hasItem(bill.getId(), BillItemType.LATE_FEE, "Late Return Fine")) {
                long lateDays = Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(res.getDueDate().toLocalDate(), now.toLocalDate()));
                BigDecimal lateFee = new BigDecimal("25.00").multiply(BigDecimal.valueOf(lateDays));
                billRepo.addItem(bill.getId(), BillItemType.LATE_FEE, lateFee, "Late Return Fine");
                notifyRepo.create(res.getId(), NotificationType.LATE_FEE_ADDED, "Late fee of $" + lateFee + " has been added to your bill.");
            }
            // Manual assessment fine
            if (damageFee != null && damageFee.compareTo(BigDecimal.ZERO) > 0) {
                billRepo.addItem(bill.getId(), BillItemType.DAMAGE_FEE, damageFee, "Damage Fee");
                notifyRepo.create(res.getId(), NotificationType.DAMAGE_FEE_ADDED, "Damage fee of $" + damageFee + " has been added to your bill.");
            }
            if (fuelFee != null && fuelFee.compareTo(BigDecimal.ZERO) > 0) {
                billRepo.addItem(bill.getId(), BillItemType.FUEL_FEE, fuelFee, "Fuel Fee");
                notifyRepo.create(res.getId(), NotificationType.FUEL_FEE_ADDED, "Fuel fee of $" + fuelFee + " has been added to your bill.");
            }
            billRepo.updateTotal(bill.getId());
        }

        // 4. Log Condition and Maintenance
        logRepo.addLog(res.getVehicleId(), VehicleLogType.CLEANING_SERVICE,
                "Vehicle returned. Mileage: " + newMileage + ", Fuel: " + fuelLevel + "%, Cleaned: " + cleaned +
                        ", Maintenance required: " + maintenanceRequired + ", Damage: " + damageDescription,
                staffAccountId);
        notifyRepo.create(res.getId(), NotificationType.RETURN_CONFIRMATION, "Vehicle returned successfully. Mileage updated to " + newMileage + ". Thank you!");
    }

    public com.cs210.project.models.VehicleReservation findReservationByBarcode(String barcode) throws Exception {
        com.cs210.project.models.Vehicle v = vehicleRepo.findByBarcode(barcode);
        if (v == null) throw new Exception("No vehicle found with barcode: " + barcode);
        
        com.cs210.project.models.VehicleReservation res = resRepo.findActiveByVehicleId(v.getId());
        if (res == null) throw new Exception("No active reservation found for this vehicle.");
        
        return res;
    }

    public Bill prepareBillForPayment(String reservationNumber) throws Exception {
        VehicleReservation res = resRepo.findByNumber(reservationNumber);
        if (res == null) throw new Exception("Reservation not found.");
        return ensureBillExists(res);
    }

    private Bill ensureBillFullyPaid(VehicleReservation res) throws Exception {
        Bill bill = ensureBillExists(res);

        BigDecimal paid = paymentRepo.getSuccessfulPaidAmount(bill.getId());
        if (paid.compareTo(bill.getTotalAmount()) < 0) {
            throw new Exception("Payment required before pickup. Remaining balance: $" + bill.getTotalAmount().subtract(paid));
        }
        return bill;
    }

    private Bill ensureBillExists(VehicleReservation res) throws Exception {
        Bill bill = billRepo.findByReservationId(res.getId());
        if (bill == null) {
            bill = new Bill();
            bill.setReservationId(res.getId());
            billRepo.create(bill);

            com.cs210.project.models.Vehicle v = vehicleRepo.findById(res.getVehicleId());
            long days = java.time.temporal.ChronoUnit.DAYS.between(res.getPickupDate().toLocalDate(), res.getDueDate().toLocalDate());
            if (days < 1) days = 1;
            BigDecimal baseAmount = BigDecimal.valueOf(v.getPricePerDay() * days);

            billRepo.addItem(bill.getId(), BillItemType.BASE_CHARGE, baseAmount, "Base Rental Charge (" + days + " days)");
            billRepo.updateTotal(bill.getId());
            bill = billRepo.findByReservationId(res.getId());
        }
        return bill;
    }

    public void processPickup(String vehicleBarcode, String memberLicense) throws Exception {
        // 1. Scan the barcode of the vehicle
        com.cs210.project.models.Vehicle v = vehicleRepo.findByBarcode(vehicleBarcode);
        if (v == null) throw new Exception("Vehicle not found with barcode: " + vehicleBarcode);

        // 2. Scan driving license or search customer (here we use license)
        // Check if member exists
        String sqlMember = "SELECT m.id FROM members m WHERE m.driver_license_number = ?";
        int memberId = -1;
        try (java.sql.Connection conn = com.cs210.project.config.DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sqlMember)) {
            pstmt.setString(1, memberLicense);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) memberId = rs.getInt("id");
            }
        }
        if (memberId == -1) throw new Exception("Member not found with license: " + memberLicense);

        // 3. Check if the customer has a valid reservation for the vehicle
        VehicleReservation res = resRepo.findPendingByVehicleAndMember(v.getId(), memberId);
        if (res == null) throw new Exception("No valid CONFIRMED reservation found for this vehicle and member.");
        eligibilityService.validateCustomerEligibility(res.getMemberId());
        eligibilityService.validateNoFailedPaymentForReservation(res.getId());
        ensureBillFullyPaid(res);

        // 4. Update status of the vehicle to 'Loaned'
        vehicleRepo.updateStatus(v.getId(), VehicleStatus.LOANED);

        // 5. Mark reservation status (following diagram: 'Completed', but using logic: 'PENDING'/Active)
        // Note: The diagram says 'Completed', but we'll use our Enums. COMPLETED usually means returned.
        // Let's use CONFIRMED -> PENDING (meaning in progress)
        resRepo.updateStatus(res.getId(), ReservationStatus.PENDING);

        // 6. Send notification
        notifyRepo.create(res.getId(), NotificationType.SYSTEM, "Vehicle " + v.getLicenseNumber() + " picked up successfully!");
        
        logRepo.addLog(v.getId(), VehicleLogType.OTHER, "Vehicle picked up via barcode scan flow.", null);
    }
}
