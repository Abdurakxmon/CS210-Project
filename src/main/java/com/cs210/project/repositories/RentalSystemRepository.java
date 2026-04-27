package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.models.RentalSystem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalSystemRepository {
    public List<RentalSystem> findAll() {
        List<RentalSystem> list = new ArrayList<>();
        String sql = "SELECT * FROM car_rental_systems";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToRentalSystem(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void create(RentalSystem system) {
        String sql = "INSERT INTO car_rental_systems (name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, system.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(RentalSystem system) {
        String sql = "UPDATE car_rental_systems SET name = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, system.getName());
            pstmt.setInt(2, system.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int id) {
        String sql = "DELETE FROM car_rental_systems WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private RentalSystem mapResultSetToRentalSystem(ResultSet rs) throws SQLException {
        RentalSystem s = new RentalSystem();
        s.setId(rs.getInt("id"));
        s.setName(rs.getString("name"));
        return s;
    }
}
