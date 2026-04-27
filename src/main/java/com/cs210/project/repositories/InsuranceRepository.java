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

    public List<RentalInsurance> findByReservationId(int resId) {
        List<RentalInsurance> list = new ArrayList<>();
        String sql = "SELECT * FROM rental_insurances WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, resId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RentalInsurance ins = new RentalInsurance();
                    ins.setId(rs.getInt("id"));
                    ins.setReservationId(rs.getInt("reservation_id"));
                    ins.setInsuranceType(InsuranceType.values()[rs.getInt("insurance_type") - 1]);
                    ins.setPrice(rs.getBigDecimal("price"));
                    list.add(ins);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
