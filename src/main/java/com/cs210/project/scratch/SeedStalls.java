package com.cs210.project.scratch;

import com.cs210.project.config.DatabaseConnection;
import java.sql.*;

public class SeedStalls {
    public static void main(String[] args) {
        String[] stalls = {"A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2"};
        String sql = "INSERT INTO parking_stalls (location_id, stall_number, location_identifier) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get first location ID
            int locId = 1;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id FROM locations LIMIT 1")) {
                if (rs.next()) locId = rs.getInt("id");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (String s : stalls) {
                    pstmt.setInt(1, locId);
                    pstmt.setString(2, s);
                    pstmt.setString(3, "Main Garage");
                    pstmt.executeUpdate();
                }
            }
            System.out.println("Seeded " + stalls.length + " stalls for location ID: " + locId);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
