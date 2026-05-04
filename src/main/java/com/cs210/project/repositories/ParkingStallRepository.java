package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.models.ParkingStall;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParkingStallRepository {
    private ParkingStall mapStall(ResultSet rs) throws SQLException {
        ParkingStall s = new ParkingStall();
        s.setId(rs.getInt("id"));
        s.setLocationId(rs.getInt("location_id"));
        s.setStallNumber(rs.getString("stall_number"));
        s.setLocationIdentifier(rs.getString("location_identifier"));
        try { s.setLocationName(rs.getString("loc_name")); } catch (Exception e) {}
        try { s.setAssignedVehicleName(rs.getString("vehicle_name")); } catch (Exception e) {}
        try { s.setAssignedVehiclePlate(rs.getString("license_number")); } catch (Exception e) {}
        return s;
    }

    public List<ParkingStall> findAvailableStalls(int locationId) {
        List<ParkingStall> stalls = new ArrayList<>();
        // Find stalls at this location that are NOT currently assigned to an active vehicle
        String sql = "SELECT s.* FROM parking_stalls s " +
                     "LEFT JOIN vehicles v ON s.id = v.parking_stall_id AND v.is_active = TRUE " +
                     "WHERE s.location_id = ? AND v.id IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, locationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stalls.add(mapStall(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return stalls;
    }


    public ParkingStall findById(int id) {
        String sql = "SELECT * FROM parking_stalls WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapStall(rs);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void create(ParkingStall s) {
        String sql = "INSERT INTO parking_stalls (location_id, stall_number, location_identifier) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, s.getLocationId());
            pstmt.setString(2, s.getStallNumber());
            pstmt.setString(3, s.getLocationIdentifier());
            pstmt.executeUpdate();
            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) s.setId(gk.getInt(1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(ParkingStall s) {
        String sql = "UPDATE parking_stalls SET location_id = ?, stall_number = ?, location_identifier = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, s.getLocationId());
            pstmt.setString(2, s.getStallNumber());
            pstmt.setString(3, s.getLocationIdentifier());
            pstmt.setInt(4, s.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int id) {
        String sql = "DELETE FROM parking_stalls WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<ParkingStall> findAll() {
        List<ParkingStall> stalls = new ArrayList<>();
        String sql = "SELECT s.*, l.name as loc_name, " +
                     "CONCAT(v.make, ' ', v.model) as vehicle_name, v.license_number " +
                     "FROM parking_stalls s " +
                     "JOIN locations l ON s.location_id = l.id " +
                     "LEFT JOIN vehicles v ON s.id = v.parking_stall_id AND v.is_active = TRUE " +
                     "ORDER BY l.name, s.stall_number";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stalls.add(mapStall(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return stalls;
    }
}
