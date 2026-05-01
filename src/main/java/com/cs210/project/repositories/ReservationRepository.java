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
        String sql = "SELECT r.*, v.make, v.model, v.license_number, pm.name as member_name, ps.name as staff_name FROM vehicle_reservations r " +
                     "JOIN vehicles v ON r.vehicle_id = v.id " +
                     "JOIN members m ON r.member_id = m.id " +
                     "JOIN accounts am ON m.id = am.id " +
                     "JOIN persons pm ON am.person_id = pm.id " +
                     "LEFT JOIN accounts as_ ON r.processed_by_account_id = as_.id " +
                     "LEFT JOIN persons ps ON as_.person_id = ps.id " +
                     "ORDER BY r.creation_date DESC";
        return fetchReservations(sql, null);
    }

    public VehicleReservation findByNumber(String resNum) {
        String sql = "SELECT r.*, v.make, v.model, v.license_number, pm.name as member_name, ps.name as staff_name FROM vehicle_reservations r " +
                     "JOIN vehicles v ON r.vehicle_id = v.id " +
                     "JOIN members m ON r.member_id = m.id " +
                     "JOIN accounts am ON m.account_id = am.id " +
                     "JOIN persons pm ON am.person_id = pm.id " +
                     "LEFT JOIN accounts as_ ON r.processed_by_account_id = as_.id " +
                     "LEFT JOIN persons ps ON as_.person_id = ps.id " +
                     "WHERE r.reservation_number = ?";
        List<VehicleReservation> list = fetchReservations(sql, resNum);
        return list.isEmpty() ? null : list.get(0);
    }

    public VehicleReservation findPendingByVehicleAndMember(int vehicleId, int memberId) {
        String sql = "SELECT r.*, v.make, v.model, v.license_number FROM vehicle_reservations r " +
                     "JOIN vehicles v ON r.vehicle_id = v.id " +
                     "WHERE r.vehicle_id = ? AND r.member_id = ? AND r.status = 3 LIMIT 1"; // 3=CONFIRMED
        List<VehicleReservation> list = fetchReservations(sql, new Object[]{vehicleId, memberId});
        return list.isEmpty() ? null : list.get(0);
    }

    public VehicleReservation findActiveByVehicleId(int vehicleId) {
        String sql = "SELECT r.*, v.make, v.model, v.license_number FROM vehicle_reservations r " +
                     "JOIN vehicles v ON r.vehicle_id = v.id " +
                     "WHERE r.vehicle_id = ? AND r.status IN (2, 3) LIMIT 1"; // 2=PENDING, 3=CONFIRMED
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

    public void update(VehicleReservation res) {
        String sql = "UPDATE vehicle_reservations SET member_id = ?, vehicle_id = ?, status = ?, due_date = ?, pickup_location_id = ?, return_location_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, res.getMemberId());
            pstmt.setInt(2, res.getVehicleId());
            pstmt.setInt(3, res.getStatus().getValue());
            pstmt.setTimestamp(4, Timestamp.valueOf(res.getDueDate()));
            pstmt.setInt(5, res.getPickupLocationId());
            pstmt.setInt(6, res.getReturnLocationId());
            pstmt.setInt(7, res.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int id) {
        String sql = "DELETE FROM vehicle_reservations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private List<VehicleReservation> fetchReservations(String sql, Object param) {
        List<VehicleReservation> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (param != null) {
                if (param instanceof Object[]) {
                    Object[] params = (Object[]) param;
                    for (int i = 0; i < params.length; i++) {
                        if (params[i] instanceof Integer) pstmt.setInt(i + 1, (Integer) params[i]);
                        else if (params[i] instanceof String) pstmt.setString(i + 1, (String) params[i]);
                    }
                } else if (param instanceof Integer) {
                    pstmt.setInt(1, (Integer) param);
                } else if (param instanceof String) {
                    pstmt.setString(1, (String) param);
                }
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
                    
                    // New fields
                    try { r.setMemberName(rs.getString("member_name")); } catch (Exception e) {}
                    try { r.setStaffName(rs.getString("staff_name")); } catch (Exception e) {}
                    
                    list.add(r);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
