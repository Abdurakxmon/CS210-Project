package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.ServiceType;
import com.cs210.project.models.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRepository {
    public void add(Service s) {
        String sql = "INSERT INTO services (reservation_id, service_type, price) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, s.getReservationId());
            pstmt.setInt(2, s.getServiceType().getValue());
            pstmt.setBigDecimal(3, s.getPrice());
            pstmt.executeUpdate();
            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) s.setId(gk.getInt(1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
