package com.cs210.project.dao;

import com.cs210.project.DatabaseManager;
import com.cs210.project.models.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    public void create(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (" +
                "inventory_id, location_id, parking_stall_id, barcode_id, vehicle_type, " +
                "category, license_number, stock_number, passenger_capacity, has_sunroof, " +
                "status, model, make, manufacturing_year, mileage) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setPreparedStatementArgs(pstmt, vehicle);
            
            pstmt.executeUpdate();

            // Retrieve the auto-generated ID from DB
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vehicle.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Vehicle findById(long id) {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractVehicleFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    public boolean update(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET " +
                "inventory_id = ?, location_id = ?, parking_stall_id = ?, barcode_id = ?, vehicle_type = ?, " +
                "category = ?, license_number = ?, stock_number = ?, passenger_capacity = ?, has_sunroof = ?, " +
                "status = ?, model = ?, make = ?, manufacturing_year = ?, mileage = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setPreparedStatementArgs(pstmt, vehicle);
            pstmt.setLong(16, vehicle.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(long id) {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to set common parameters for Create and Update queries
    private void setPreparedStatementArgs(PreparedStatement pstmt, Vehicle vehicle) throws SQLException {
        if (vehicle.getInventoryId() != null) pstmt.setLong(1, vehicle.getInventoryId());
        else pstmt.setNull(1, Types.BIGINT);

        pstmt.setLong(2, vehicle.getLocationId());

        if (vehicle.getParkingStallId() != null) pstmt.setLong(3, vehicle.getParkingStallId());
        else pstmt.setNull(3, Types.BIGINT);

        pstmt.setLong(4, vehicle.getBarcodeId());
        pstmt.setString(5, vehicle.getVehicleType()); // Enum in DB

        if (vehicle.getCategory() != null) pstmt.setString(6, vehicle.getCategory());
        else pstmt.setNull(6, Types.VARCHAR);

        pstmt.setString(7, vehicle.getLicenseNumber());
        pstmt.setString(8, vehicle.getStockNumber());

        if (vehicle.getPassengerCapacity() != null) pstmt.setInt(9, vehicle.getPassengerCapacity());
        else pstmt.setNull(9, Types.INTEGER);

        if (vehicle.getHasSunroof() != null) pstmt.setBoolean(10, vehicle.getHasSunroof());
        else pstmt.setNull(10, Types.BOOLEAN);

        pstmt.setString(11, vehicle.getStatus()); // Enum in DB

        if (vehicle.getModel() != null) pstmt.setString(12, vehicle.getModel());
        else pstmt.setNull(12, Types.VARCHAR);

        if (vehicle.getMake() != null) pstmt.setString(13, vehicle.getMake());
        else pstmt.setNull(13, Types.VARCHAR);

        if (vehicle.getManufacturingYear() != null) pstmt.setInt(14, vehicle.getManufacturingYear());
        else pstmt.setNull(14, Types.INTEGER);

        if (vehicle.getMileage() != null) pstmt.setInt(15, vehicle.getMileage());
        else pstmt.setNull(15, Types.INTEGER);
    }

    // Helper method to extract Vehicle from ResultSet considering Null values properly
    private Vehicle extractVehicleFromResultSet(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();

        vehicle.setId(rs.getLong("id"));

        long inventoryId = rs.getLong("inventory_id");
        vehicle.setInventoryId(rs.wasNull() ? null : inventoryId);

        vehicle.setLocationId(rs.getLong("location_id"));

        long parkingStallId = rs.getLong("parking_stall_id");
        vehicle.setParkingStallId(rs.wasNull() ? null : parkingStallId);

        vehicle.setBarcodeId(rs.getLong("barcode_id"));
        vehicle.setVehicleType(rs.getString("vehicle_type"));
        vehicle.setCategory(rs.getString("category"));
        vehicle.setLicenseNumber(rs.getString("license_number"));
        vehicle.setStockNumber(rs.getString("stock_number"));

        int capacity = rs.getInt("passenger_capacity");
        vehicle.setPassengerCapacity(rs.wasNull() ? null : capacity);

        boolean hasSunroof = rs.getBoolean("has_sunroof");
        vehicle.setHasSunroof(rs.wasNull() ? null : hasSunroof);

        vehicle.setStatus(rs.getString("status"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setMake(rs.getString("make"));

        int year = rs.getInt("manufacturing_year");
        vehicle.setManufacturingYear(rs.wasNull() ? null : year);

        int mileage = rs.getInt("mileage");
        vehicle.setMileage(rs.wasNull() ? null : mileage);

        vehicle.setCreatedAt(rs.getTimestamp("created_at"));
        vehicle.setUpdatedAt(rs.getTimestamp("updated_at"));

        return vehicle;
    }
}
