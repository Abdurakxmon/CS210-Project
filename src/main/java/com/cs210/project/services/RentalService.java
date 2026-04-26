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

    public void pickupVehicle(String reservationNumber, int staffAccountId) throws Exception {
        VehicleReservation res = resRepo.findByNumber(reservationNumber);
        if (res == null) throw new Exception("Reservation not found.");
        if (res.getStatus() != ReservationStatus.CONFIRMED) throw new Exception("Reservation is not in CONFIRMED status.");

        vehicleRepo.updateStatus(res.getVehicleId(), VehicleStatus.LOANED);
        resRepo.updateStatus(res.getId(), ReservationStatus.PENDING); // Mark as active
        
        logRepo.addLog(res.getVehicleId(), VehicleLogType.OTHER, "Vehicle picked up for reservation " + reservationNumber, staffAccountId);
        notifyRepo.create(res.getId(), NotificationType.SYSTEM, "Vehicle picked up. Drive safely!");
    }

    public void returnVehicle(String reservationNumber, int staffAccountId) throws Exception {
        VehicleReservation res = resRepo.findByNumber(reservationNumber);
        if (res == null) throw new Exception("Reservation not found.");

        LocalDateTime now = LocalDateTime.now();
        
        // Update reservation with return date and staff info
        String sqlUpdate = "UPDATE vehicle_reservations SET status = ?, return_date = ?, processed_by_account_id = ? WHERE id = ?";
        try (java.sql.Connection conn = com.cs210.project.config.DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
            pstmt.setInt(1, ReservationStatus.COMPLETED.getValue());
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(now));
            pstmt.setInt(3, staffAccountId);
            pstmt.setInt(4, res.getId());
            pstmt.executeUpdate();
        }

        vehicleRepo.updateStatus(res.getVehicleId(), VehicleStatus.AVAILABLE);
        
        // Handle Fines
        if (now.isAfter(res.getDueDate())) {
            Bill bill = billRepo.findByReservationId(res.getId());
            if (bill != null) {
                billRepo.addItem(bill.getId(), BillItemType.FINE, new BigDecimal("25.00"), "Late Return Fine");
            }
        }

        logRepo.addLog(res.getVehicleId(), VehicleLogType.CLEANING_SERVICE, "Vehicle returned and sent for cleaning.", staffAccountId);
        notifyRepo.create(res.getId(), NotificationType.SYSTEM, "Vehicle returned successfully. Thank you!");
        
        // Update return date in DB
        // (Implementation missing in Repo, adding here via direct SQL or assume Repo update)
    }

    public com.cs210.project.models.VehicleReservation findReservationByBarcode(String barcode) throws Exception {
        com.cs210.project.models.Vehicle v = vehicleRepo.findByBarcode(barcode);
        if (v == null) throw new Exception("No vehicle found with barcode: " + barcode);
        
        com.cs210.project.models.VehicleReservation res = resRepo.findActiveByVehicleId(v.getId());
        if (res == null) throw new Exception("No active reservation found for this vehicle.");
        
        return res;
    }
}
