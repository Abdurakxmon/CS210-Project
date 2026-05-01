package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.models.ReturnInspection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

public class ReturnInspectionRepository {
    public void create(ReturnInspection inspection) {
        String sql = "INSERT INTO return_inspections (reservation_id, vehicle_id, worker_account_id, inspection_date, mileage, fuel_level, damage_description, damage_fee, fuel_fee, cleaned, maintenance_required, parking_stall_id, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, inspection.getReservationId());
            pstmt.setInt(2, inspection.getVehicleId());
            pstmt.setInt(3, inspection.getWorkerAccountId());
            pstmt.setTimestamp(4, Timestamp.valueOf(inspection.getInspectionDate()));
            pstmt.setInt(5, inspection.getMileage());
            pstmt.setInt(6, inspection.getFuelLevel());
            pstmt.setString(7, inspection.getDamageDescription());
            pstmt.setBigDecimal(8, inspection.getDamageFee());
            pstmt.setBigDecimal(9, inspection.getFuelFee());
            pstmt.setBoolean(10, inspection.isCleaned());
            pstmt.setBoolean(11, inspection.isMaintenanceRequired());
            if (inspection.getParkingStallId() != null) pstmt.setInt(12, inspection.getParkingStallId()); else pstmt.setNull(12, Types.INTEGER);
            pstmt.setString(13, inspection.getNotes());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
