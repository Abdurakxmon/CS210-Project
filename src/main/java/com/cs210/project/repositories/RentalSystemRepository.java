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
                RentalSystem s = new RentalSystem();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                list.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
