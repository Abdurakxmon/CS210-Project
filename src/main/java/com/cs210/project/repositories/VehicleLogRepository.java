package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.VehicleLogType;

import java.sql.*;
import java.time.LocalDateTime;

public class VehicleLogRepository {
    public void addLog(int vehicleId, VehicleLogType type, String description, Integer accountId) {
        String sql = "INSERT INTO vehicle_logs (vehicle_id, log_type, description, creation_date, account_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            pstmt.setInt(2, type.getValue());
            pstmt.setString(3, description);
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            if (accountId != null) pstmt.setInt(5, accountId); else pstmt.setNull(5, Types.INTEGER);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public java.util.List<String> getAuditLogsForVehicle(int vehicleId) {
        java.util.List<String> logs = new java.util.ArrayList<>();
        String sql = "SELECT l.*, p.name as staff_name FROM vehicle_logs l " +
                     "LEFT JOIN accounts a ON l.account_id = a.id " +
                     "LEFT JOIN persons p ON a.person_id = p.id " +
                     "WHERE l.vehicle_id = ? ORDER BY l.creation_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String staff = rs.getString("staff_name") != null ? rs.getString("staff_name") : "System";
                    logs.add(String.format("[%s] By: %s | %s", 
                        rs.getTimestamp("creation_date"), staff, rs.getString("description")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return logs;
    }
}
