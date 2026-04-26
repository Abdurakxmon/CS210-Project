package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.ReservationStatus;
import com.cs210.project.models.VehicleReservation;
import com.cs210.project.models.Vehicle;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {

    public String generateReservationNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sql = "SELECT COUNT(*) FROM vehicle_reservations WHERE reservation_number LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "RES-" + datePart + "-%");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int nextNum = rs.getInt(1) + 1;
                    return String.format("RES-%s-%06d", datePart, nextNum);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "RES-" + datePart + "-000001";
    }

    public List<VehicleReservation> findByMemberId(int memberId) {
        String sql = "SELECT r.*, v.make, v.model, v.license_number FROM vehicle_reservations r " +
                     "JOIN vehicles v ON r.vehicle_id = v.id WHERE r.member_id = ? ORDER BY r.creation_date DESC";
        return fetchReservations(sql, memberId);
    }

    public List<VehicleReservation> findAll() {
        String sql = "SELECT r.*, v.make, v.model, v.license_number FROM vehicle_reservations r " +
                     "JOIN vehicles v ON r.vehicle_id = v.id ORDER BY r.creation_date DESC";
        return fetchReservations(sql, null);
    }

    public VehicleReservation findByNumber(String resNum) {
        String sql = "SELECT r.*, v.make, v.model, v.license_number FROM vehicle_reservations r " +
                     "JOIN vehicles v ON r.vehicle_id = v.id WHERE r.reservation_number = ?";
        List<VehicleReservation> list = fetchReservations(sql, resNum);
        return list.isEmpty() ? null : list.get(0);
    }

    public VehicleReservation findActiveByVehicleId(int vehicleId) {
        // Find reservation that is CONFIRMED (ready for pickup) or PENDING (currently in use)
        String sql = "SELECT r.*, v.make, v.model, v.license_number FROM vehicle_reservations r " +
                     "JOIN vehicles v ON r.vehicle_id = v.id " +
                     "WHERE r.vehicle_id = ? AND r.status IN (1, 2) LIMIT 1"; // 1=CONFIRMED, 2=PENDING
        List<VehicleReservation> list = fetchReservations(sql, vehicleId);
        return list.isEmpty() ? null : list.get(0);
    }

    public void updateStatus(int id, ReservationStatus status) {
        String sql = "UPDATE vehicle_reservations SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, status.getValue());
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private List<VehicleReservation> fetchReservations(String sql, Object param) {
        List<VehicleReservation> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (param != null) {
                if (param instanceof Integer) pstmt.setInt(1, (Integer) param);
                else if (param instanceof String) pstmt.setString(1, (String) param);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    VehicleReservation r = new VehicleReservation();
                    r.setId(rs.getInt("id"));
                    r.setReservationNumber(rs.getString("reservation_number"));
                    r.setMemberId(rs.getInt("member_id"));
                    r.setVehicleId(rs.getInt("vehicle_id"));
                    r.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
                    r.setStatus(ReservationStatus.fromInt(rs.getInt("status")));
                    r.setDueDate(rs.getTimestamp("due_date").toLocalDateTime());
                    Timestamp retDate = rs.getTimestamp("return_date");
                    if (retDate != null) r.setReturnDate(retDate.toLocalDateTime());
                    r.setPickupLocationId(rs.getInt("pickup_location_id"));
                    r.setReturnLocationId(rs.getInt("return_location_id"));
                    
                    // Transient vehicle info for display
                    r.setVehicleMake(rs.getString("make"));
                    r.setVehicleModel(rs.getString("model"));
                    r.setVehiclePlate(rs.getString("license_number"));
                    
                    list.add(r);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
