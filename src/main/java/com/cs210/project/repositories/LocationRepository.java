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
                list.add(mapResultSetToLocation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void create(Location loc) {
        String sql = "INSERT INTO locations (system_id, name, street_address, city, state, zipcode, country) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, loc.getSystemId());
            pstmt.setString(2, loc.getName());
            pstmt.setString(3, loc.getStreetAddress());
            pstmt.setString(4, loc.getCity());
            pstmt.setString(5, loc.getState());
            pstmt.setString(6, loc.getZipcode());
            pstmt.setString(7, loc.getCountry());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(Location loc) {
        String sql = "UPDATE locations SET system_id = ?, name = ?, street_address = ?, city = ?, state = ?, zipcode = ?, country = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, loc.getSystemId());
            pstmt.setString(2, loc.getName());
            pstmt.setString(3, loc.getStreetAddress());
            pstmt.setString(4, loc.getCity());
            pstmt.setString(5, loc.getState());
            pstmt.setString(6, loc.getZipcode());
            pstmt.setString(7, loc.getCountry());
            pstmt.setInt(8, loc.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int id) {
        String sql = "DELETE FROM locations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Location mapResultSetToLocation(ResultSet rs) throws SQLException {
        Location loc = new Location();
        loc.setId(rs.getInt("id"));
        loc.setSystemId(rs.getInt("system_id"));
        loc.setName(rs.getString("name"));
        loc.setStreetAddress(rs.getString("street_address"));
        loc.setCity(rs.getString("city"));
        loc.setState(rs.getString("state"));
        loc.setZipcode(rs.getString("zipcode"));
        loc.setCountry(rs.getString("country"));
        return loc;
    }
}
