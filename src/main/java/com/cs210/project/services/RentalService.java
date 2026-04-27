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

    public void pickupVehicle(String reservationNumber, int staffAccountId) throws Exception {
        VehicleReservation res = resRepo.findByNumber(reservationNumber);
        if (res == null) throw new Exception("Reservation not found.");
        if (res.getStatus() != ReservationStatus.CONFIRMED) throw new Exception("Reservation is not in CONFIRMED status.");

        // 1. Check/Create Bill
        Bill bill = billRepo.findByReservationId(res.getId());
        if (bill == null) {
            bill = new Bill();
            bill.setReservationId(res.getId());
            billRepo.create(bill);
            
            // Calculate base price
            com.cs210.project.models.Vehicle v = vehicleRepo.findById(res.getVehicleId());
            long days = java.time.Duration.between(res.getCreationDate(), res.getDueDate()).toDays();
            if (days < 1) days = 1;
            BigDecimal baseAmount = BigDecimal.valueOf(v.getPricePerDay() * days);
            
            billRepo.addItem(bill.getId(), BillItemType.BASE_CHARGE, baseAmount, "Base Rental Charge (" + days + " days)");
            billRepo.updateTotal(bill.getId());
        }

        // 2. Check Payment Status
        BigDecimal paid = paymentRepo.findByBillId(bill.getId()).stream()
            .map(com.cs210.project.models.Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        if (paid.compareTo(bill.getTotalAmount()) < 0) {
            throw new Exception("Payment required. Remaining balance: $" + bill.getTotalAmount().subtract(paid));
        }

        // 3. Update Statuses ONLY AFTER PAYMENT IS VERIFIED
        vehicleRepo.updateStatus(res.getVehicleId(), VehicleStatus.LOANED);
        resRepo.updateStatus(res.getId(), ReservationStatus.PENDING); // Mark as active

        logRepo.addLog(res.getVehicleId(), VehicleLogType.OTHER, "Vehicle picked up for reservation " + reservationNumber + ". Bill prepared.", staffAccountId);
        notifyRepo.create(res.getId(), NotificationType.SYSTEM, "Vehicle picked up. Your rental has started!");
    }

    public void initiateReturn(String reservationNumber) throws Exception {
        VehicleReservation res = resRepo.findByNumber(reservationNumber);
        if (res == null) throw new Exception("Reservation not found.");
        if (res.getStatus() != ReservationStatus.PENDING) throw new Exception("Vehicle is not currently out for rental.");
        
        resRepo.updateStatus(res.getId(), ReservationStatus.WAITING_FOR_INSPECTION);
        notifyRepo.create(res.getId(), NotificationType.SYSTEM, "Return initiated. Please leave the vehicle at the designated area for inspection.");
    }

    public void returnVehicle(String reservationNumber, int staffAccountId, int newMileage, String conditionLog, BigDecimal manualFine) throws Exception {
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

        // 2. Update Vehicle Status and Mileage
        vehicleRepo.updateStatus(res.getVehicleId(), VehicleStatus.AVAILABLE);
        vehicleRepo.updateMileage(res.getVehicleId(), newMileage);
        
        // 3. Handle Fines
        Bill bill = billRepo.findByReservationId(res.getId());
        if (bill != null) {
            // Late return fine
            if (now.isAfter(res.getDueDate())) {
                billRepo.addItem(bill.getId(), BillItemType.FINE, new BigDecimal("25.00"), "Late Return Fine");
            }
            // Manual assessment fine
            if (manualFine != null && manualFine.compareTo(BigDecimal.ZERO) > 0) {
                billRepo.addItem(bill.getId(), BillItemType.FINE, manualFine, "Damage/Other Assessment Fine");
                notifyRepo.create(res.getId(), NotificationType.SYSTEM, "A fine of $" + manualFine + " has been added to your bill for damage/other issues.");
            }
            billRepo.updateTotal(bill.getId());
        }

        // 4. Log Condition and Maintenance
        logRepo.addLog(res.getVehicleId(), VehicleLogType.CLEANING_SERVICE, "Vehicle returned. Condition: " + conditionLog, staffAccountId);
        notifyRepo.create(res.getId(), NotificationType.SYSTEM, "Vehicle returned successfully. Mileage updated to " + newMileage + ". Thank you!");
    }

    public com.cs210.project.models.VehicleReservation findReservationByBarcode(String barcode) throws Exception {
        com.cs210.project.models.Vehicle v = vehicleRepo.findByBarcode(barcode);
        if (v == null) throw new Exception("No vehicle found with barcode: " + barcode);
        
        com.cs210.project.models.VehicleReservation res = resRepo.findActiveByVehicleId(v.getId());
        if (res == null) throw new Exception("No active reservation found for this vehicle.");
        
        return res;
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
