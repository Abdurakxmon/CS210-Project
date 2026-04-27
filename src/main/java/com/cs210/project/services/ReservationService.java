package com.cs210.project.services;

import com.cs210.project.constants.Enums.*;
import com.cs210.project.constants.VehicleStatus;
import com.cs210.project.models.Vehicle;
import com.cs210.project.models.VehicleReservation;
import com.cs210.project.repositories.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import com.cs210.project.config.DatabaseConnection;

public class ReservationService {
    private final ReservationRepository resRepo = new ReservationRepository();
    private final VehicleRepository vehicleRepo = new VehicleRepository();
    private final BillRepository billRepo = new BillRepository();
    private final NotificationRepository notifyRepo = new NotificationRepository();
    private final EquipmentRepository equipmentRepo = new EquipmentRepository();
    private final InsuranceRepository insuranceRepo = new InsuranceRepository();
    private final ServiceRepository serviceRepo = new ServiceRepository();

    public String createReservation(int memberId, int vehicleId, int pickupLoc, int returnLoc, LocalDateTime dueDate,
                                    List<InsuranceType> insurances, List<EquipmentType> equipments, List<ServiceType> services) throws Exception {
        Vehicle v = vehicleRepo.findById(vehicleId);
        if (v == null || v.getStatus() != VehicleStatus.AVAILABLE) {
            throw new Exception("Vehicle is not available for reservation.");
        }

        String resNumber = resRepo.generateReservationNumber();
        
        String sql = "INSERT INTO vehicle_reservations (reservation_number, member_id, vehicle_id, creation_date, status, due_date, pickup_location_id, return_location_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, resNumber);
            pstmt.setInt(2, memberId);
            pstmt.setInt(3, vehicleId);
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(5, ReservationStatus.CONFIRMED.getValue());
            pstmt.setTimestamp(6, Timestamp.valueOf(dueDate));
            pstmt.setInt(7, pickupLoc);
            pstmt.setInt(8, returnLoc);
            pstmt.executeUpdate();

            int resId;
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) resId = rs.getInt(1); else throw new SQLException("Reservation failed");
            }

            // Update Vehicle Status
            vehicleRepo.updateStatus(vehicleId, VehicleStatus.RESERVED);

            // Create Initial Bill
            com.cs210.project.models.Bill bill = new com.cs210.project.models.Bill();
            bill.setReservationId(resId);
            billRepo.create(bill);
            
            // 1. Base Charge
            long days = java.time.Duration.between(LocalDateTime.now(), dueDate).toDays();
            if (days < 1) days = 1;
            BigDecimal baseAmount = BigDecimal.valueOf(v.getPricePerDay() * days);
            billRepo.addItem(bill.getId(), BillItemType.BASE_CHARGE, baseAmount, "Base Rental Charge (" + days + " days)");

            // 2. Add Insurances
            for (InsuranceType type : insurances) {
                BigDecimal price = new BigDecimal("15.00"); // Standard price
                com.cs210.project.models.RentalInsurance ri = new com.cs210.project.models.RentalInsurance();
                ri.setReservationId(resId);
                ri.setInsuranceType(type);
                ri.setPrice(price);
                insuranceRepo.add(ri);
                billRepo.addItem(bill.getId(), BillItemType.OTHER, price, "Insurance: " + type.name());
            }

            // 3. Add Equipments
            for (EquipmentType type : equipments) {
                BigDecimal price = new BigDecimal("10.00");
                com.cs210.project.models.Equipment eq = new com.cs210.project.models.Equipment();
                eq.setReservationId(resId);
                eq.setEquipmentType(type);
                eq.setPrice(price);
                equipmentRepo.add(eq);
                billRepo.addItem(bill.getId(), BillItemType.OTHER, price, "Equipment: " + type.name());
            }

            // 4. Add Services
            for (ServiceType type : services) {
                BigDecimal price = new BigDecimal("20.00");
                com.cs210.project.models.Service s = new com.cs210.project.models.Service();
                s.setReservationId(resId);
                s.setServiceType(type);
                s.setPrice(price);
                serviceRepo.add(s);
                billRepo.addItem(bill.getId(), BillItemType.OTHER, price, "Service: " + type.name());
            }

            billRepo.updateTotal(bill.getId());

            // Create Notification
            notifyRepo.create(resId, NotificationType.SYSTEM, "Reservation " + resNumber + " confirmed for " + v.getMake() + " " + v.getModel());

            return resNumber;
        }
    }

    public List<VehicleReservation> getMyReservations(int memberId) {
        return resRepo.findByMemberId(memberId);
    }

    public List<VehicleReservation> getAllReservations() {
        return resRepo.findAll();
    }

    public void cancelReservation(int resId) throws Exception {
        // Implementation for cancellation
        String sql = "SELECT * FROM vehicle_reservations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, resId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int vehicleId = rs.getInt("vehicle_id");
                    int status = rs.getInt("status");
                    if (status > 3) throw new Exception("Cannot cancel completed or already cancelled reservation.");
                    
                    resRepo.updateStatus(resId, ReservationStatus.CANCELLED);
                    vehicleRepo.updateStatus(vehicleId, VehicleStatus.AVAILABLE);
                    notifyRepo.create(resId, NotificationType.SYSTEM, "Reservation " + rs.getString("reservation_number") + " has been cancelled.");
                }
            }
        }
    }

    public void updateReservation(VehicleReservation res) {
        resRepo.update(res);
        notifyRepo.create(res.getId(), NotificationType.SYSTEM, "Reservation " + res.getReservationNumber() + " has been updated by staff.");
    }

    public void deleteReservation(int resId) throws Exception {
        String sql = "SELECT vehicle_id FROM vehicle_reservations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, resId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int vehicleId = rs.getInt("vehicle_id");
                    vehicleRepo.updateStatus(vehicleId, VehicleStatus.AVAILABLE);
                }
            }
        }
        resRepo.delete(resId);
    }
}
