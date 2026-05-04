package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.InsuranceType;
import com.cs210.project.models.RentalInsurance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsuranceRepository {
    public void add(RentalInsurance ins) {
        String sql = "INSERT INTO rental_insurances (reservation_id, insurance_type, price) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, ins.getReservationId());
            pstmt.setInt(2, ins.getInsuranceType().getValue());
            pstmt.setBigDecimal(3, ins.getPrice());
            pstmt.executeUpdate();
            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) ins.setId(gk.getInt(1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
