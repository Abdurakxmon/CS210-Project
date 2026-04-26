package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.models.Location;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationRepository {
    public List<Location> findAll() {
        List<Location> list = new ArrayList<>();
        String sql = "SELECT * FROM locations";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Location loc = new Location();
                loc.setId(rs.getInt("id"));
                loc.setSystemId(rs.getInt("system_id"));
                loc.setName(rs.getString("name"));
                loc.setCity(rs.getString("city"));
                list.add(loc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
