package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.EquipmentType;
import com.cs210.project.models.Equipment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentRepository {
    public void add(Equipment eq) {
        String sql = "INSERT INTO equipment (reservation_id, equipment_type, price) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, eq.getReservationId());
            pstmt.setInt(2, eq.getEquipmentType().getValue());
            pstmt.setBigDecimal(3, eq.getPrice());
            pstmt.executeUpdate();
            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) eq.setId(gk.getInt(1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Equipment> findByReservationId(int resId) {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT * FROM equipment WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, resId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Equipment eq = new Equipment();
                    eq.setId(rs.getInt("id"));
                    eq.setReservationId(rs.getInt("reservation_id"));
                    eq.setEquipmentType(EquipmentType.fromInt(rs.getInt("equipment_type")));
                    eq.setPrice(rs.getBigDecimal("price"));
                    list.add(eq);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
