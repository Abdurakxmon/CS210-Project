package com.cs210.project.services;

import com.cs210.project.constants.Enums.*;
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
    private final VehicleLogRepository logRepo = new VehicleLogRepository();
    private final CustomerEligibilityService eligibilityService = new CustomerEligibilityService();

    public String createReservation(int memberId, int vehicleId, int pickupLoc, int returnLoc,
            LocalDateTime pickupDate, LocalDateTime returnDate,
            List<InsuranceType> insurances, List<EquipmentType> equipments) throws Exception {
        if (pickupDate == null || returnDate == null || !returnDate.isAfter(pickupDate)) {
            throw new Exception("Pickup and return dates are required, and return must be after pickup.");
        }

        eligibilityService.validateCustomerEligibility(memberId);

        Vehicle v = vehicleRepo.findById(vehicleId);
        if (v == null || !v.isActive()) {
            throw new Exception("Vehicle is not available.");
        }
        if (!vehicleRepo.isAvailableForDateRange(vehicleId, pickupDate, returnDate, null)) {
            throw new Exception("Vehicle is already booked for the selected dates.");
        }

        String resNumber = resRepo.generateReservationNumber();

        String sql = "INSERT INTO vehicle_reservations (reservation_number, member_id, vehicle_id, creation_date, pickup_date, status, due_date, pickup_location_id, return_location_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, resNumber);
            pstmt.setInt(2, memberId);
            pstmt.setInt(3, vehicleId);
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(5, Timestamp.valueOf(pickupDate));
            pstmt.setInt(6, ReservationStatus.CONFIRMED.getValue());
            pstmt.setTimestamp(7, Timestamp.valueOf(returnDate));
            pstmt.setInt(8, pickupLoc);
            pstmt.setInt(9, returnLoc);
            pstmt.executeUpdate();

            int resId;
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next())
                    resId = rs.getInt(1);
                else
                    throw new SQLException("Reservation failed");
            }

            // Create Initial Bill
            com.cs210.project.models.Bill bill = new com.cs210.project.models.Bill();
            bill.setReservationId(resId);
            billRepo.create(bill);

            // 1. Base Charge
            long days = java.time.temporal.ChronoUnit.DAYS.between(pickupDate.toLocalDate(), returnDate.toLocalDate());
            if (days < 1)
                days = 1;
            BigDecimal baseAmount = BigDecimal.valueOf(v.getPricePerDay() * days);
            billRepo.addItem(bill.getId(), BillItemType.BASE_CHARGE, baseAmount,
                    "Base Rental Charge (" + days + " days)");

            // 2. Add Insurances
            for (InsuranceType type : insurances) {
                BigDecimal price = insurancePrice(type);
                com.cs210.project.models.RentalInsurance ri = new com.cs210.project.models.RentalInsurance();
                ri.setReservationId(resId);
                ri.setInsuranceType(type);
                ri.setPrice(price);
                insuranceRepo.add(ri);
                billRepo.addItem(bill.getId(), BillItemType.INSURANCE, price, "Insurance: " + type.getLabel());
            }

            // 3. Add Equipments
            for (EquipmentType type : equipments) {
                BigDecimal price = equipmentPrice(type);
                com.cs210.project.models.Equipment eq = new com.cs210.project.models.Equipment();
                eq.setReservationId(resId);
                eq.setEquipmentType(type);
                eq.setPrice(price);
                equipmentRepo.add(eq);
                billRepo.addItem(bill.getId(), BillItemType.EQUIPMENT, price, "Equipment: " + type.getLabel());
            }

            billRepo.updateTotal(bill.getId());

            // Create Notification
            notifyRepo.create(resId, NotificationType.RESERVATION_CONFIRMATION,
                    "Reservation " + resNumber + " confirmed for " + v.getMake() + " " + v.getModel());
            logRepo.addLog(vehicleId, VehicleLogType.OTHER,
                    "Reservation " + resNumber + " created for " + pickupDate + " to " + returnDate + ".", null);

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
                    if (status > 3)
                        throw new Exception("Cannot cancel completed or already cancelled reservation.");

                    resRepo.updateStatus(resId, ReservationStatus.CANCELLED);

                    // Handle cancellation fee
                    com.cs210.project.models.Bill bill = billRepo.findByReservationId(resId);
                    if (bill != null) {
                        billRepo.clearItems(bill.getId());
                        billRepo.addItem(bill.getId(), BillItemType.CANCELLATION_FEE, new BigDecimal("10.00"),
                                "Cancellation Fee");
                    }

                    notifyRepo.create(resId, NotificationType.CANCELLATION_NOTIFICATION,
                            "Reservation " + rs.getString("reservation_number") + " has been cancelled.");
                    logRepo.addLog(vehicleId, VehicleLogType.OTHER,
                            "Reservation " + rs.getString("reservation_number") + " cancelled.", null);
                }
            }
        }
    }

    public void updateReservation(VehicleReservation res) {
        resRepo.update(res);
        notifyRepo.create(res.getId(), NotificationType.RESERVATION_REMINDER,
                "Reservation " + res.getReservationNumber() + " has been updated by staff.");
    }

    public void deleteReservation(int resId) throws Exception {
        String sql = "SELECT vehicle_id FROM vehicle_reservations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, resId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int vehicleId = rs.getInt("vehicle_id");
                    logRepo.addLog(vehicleId, VehicleLogType.OTHER, "Reservation deleted by staff.", null);
                }
            }
        }
        resRepo.delete(resId);
    }

    private BigDecimal insurancePrice(InsuranceType type) {
        return switch (type) {
            case BASIC -> new BigDecimal("15.00");
            case PERSONAL -> new BigDecimal("15.00");
            case BELONGINGS -> new BigDecimal("12.00");
        };
    }

    private BigDecimal equipmentPrice(EquipmentType type) {
        return switch (type) {
            case NAVIGATION -> new BigDecimal("10.00");
            case CHILD_SEAT -> new BigDecimal("7.00");
            case WIFI -> new BigDecimal("9.00");
            case CAR_FRIDGE -> new BigDecimal("12.00");
        };
    }

}
