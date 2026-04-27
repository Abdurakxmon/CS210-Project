package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.CarType;
import com.cs210.project.constants.Enums.VehicleType;
import com.cs210.project.constants.VehicleStatus;
import com.cs210.project.models.Vehicle;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepository {

    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT v.*, b.barcode, l.name as loc_name, s.name as sys_name FROM vehicles v " +
                     "JOIN barcodes b ON v.barcode_id = b.id " +
                     "JOIN locations l ON v.location_id = l.id " +
                     "JOIN car_rental_systems s ON l.system_id = s.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    public List<Vehicle> search(VehicleType type, VehicleStatus status, String model, Integer locationId, Integer systemId) {
        List<Vehicle> vehicles = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT v.*, b.barcode, l.name as loc_name, s.name as sys_name FROM vehicles v " +
                                              "JOIN barcodes b ON v.barcode_id = b.id " +
                                              "JOIN locations l ON v.location_id = l.id " +
                                              "JOIN car_rental_systems s ON l.system_id = s.id " +
                                              "WHERE 1=1");
        if (type != null) sql.append(" AND v.vehicle_type = ?");
        if (status != null) sql.append(" AND v.status = ?");
        if (model != null && !model.isBlank()) sql.append(" AND v.model LIKE ?");
        if (locationId != null) sql.append(" AND v.location_id = ?");
        if (systemId != null) sql.append(" AND l.system_id = ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIdx = 1;
            if (type != null) pstmt.setInt(paramIdx++, type.getValue());
            if (status != null) pstmt.setInt(paramIdx++, status.getValue());
            if (model != null && !model.isBlank()) pstmt.setString(paramIdx++, "%" + model + "%");
            if (locationId != null) pstmt.setInt(paramIdx++, locationId);
            if (systemId != null) pstmt.setInt(paramIdx++, systemId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    public Vehicle findById(int id) {
        String sql = "SELECT v.*, b.barcode FROM vehicles v JOIN barcodes b ON v.barcode_id = b.id WHERE v.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehicle(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Vehicle findByBarcode(String barcode) {
        String sql = "SELECT v.*, b.barcode, l.name as loc_name, s.name as sys_name FROM vehicles v " +
                     "JOIN barcodes b ON v.barcode_id = b.id " +
                     "JOIN locations l ON v.location_id = l.id " +
                     "JOIN car_rental_systems s ON l.system_id = s.id " +
                     "WHERE b.barcode = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barcode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToVehicle(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void updateStatus(int vehicleId, com.cs210.project.constants.VehicleStatus status) {
        String sql = "UPDATE vehicles SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, status.getValue());
            pstmt.setInt(2, vehicleId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateMileage(int vehicleId, int newMileage) {
        String sql = "UPDATE vehicles SET mileage = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newMileage);
            pstmt.setInt(2, vehicleId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int getOrCreateBarcode(String barcode) {
        String checkSql = "SELECT id FROM barcodes WHERE barcode = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, barcode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        String insertSql = "INSERT INTO barcodes (barcode, issued_at) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, barcode);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public void create(Vehicle v) {
        String sql = "INSERT INTO vehicles (location_id, parking_stall_id, barcode_id, vehicle_type, car_type, license_number, stock_number, passenger_capacity, has_sunroof, status, model, make, manufacturing_year, mileage, price_per_day) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, v.getLocationId());
            if (v.getParkingStallId() != null) pstmt.setInt(2, v.getParkingStallId()); else pstmt.setNull(2, Types.INTEGER);
            int bid = getOrCreateBarcode(v.getBarcode());
            pstmt.setInt(3, bid);
            pstmt.setInt(4, v.getVehicleType().getValue());
            if (v.getCarType() != null) pstmt.setInt(5, v.getCarType().getValue()); else pstmt.setNull(5, Types.INTEGER);
            pstmt.setString(6, v.getLicenseNumber());
            pstmt.setString(7, v.getStockNumber());
            pstmt.setInt(8, v.getPassengerCapacity());
            pstmt.setBoolean(9, v.isHasSunroof());
            pstmt.setInt(10, v.getStatus().getValue());
            pstmt.setString(11, v.getModel());
            pstmt.setString(12, v.getMake());
            pstmt.setInt(13, v.getManufacturingYear());
            pstmt.setInt(14, v.getMileage());
            pstmt.setDouble(15, v.getPricePerDay());
            
            pstmt.executeUpdate();
            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) v.setId(gk.getInt(1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(Vehicle v) {
        String sql = "UPDATE vehicles SET location_id=?, parking_stall_id=?, vehicle_type=?, car_type=?, license_number=?, stock_number=?, passenger_capacity=?, has_sunroof=?, status=?, model=?, make=?, manufacturing_year=?, mileage=?, price_per_day=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, v.getLocationId());
            if (v.getParkingStallId() != null) pstmt.setInt(2, v.getParkingStallId()); else pstmt.setNull(2, Types.INTEGER);
            pstmt.setInt(3, v.getVehicleType().getValue());
            if (v.getCarType() != null) pstmt.setInt(4, v.getCarType().getValue()); else pstmt.setNull(4, Types.INTEGER);
            pstmt.setString(5, v.getLicenseNumber());
            pstmt.setString(6, v.getStockNumber());
            pstmt.setInt(7, v.getPassengerCapacity());
            pstmt.setBoolean(8, v.isHasSunroof());
            pstmt.setInt(9, v.getStatus().getValue());
            pstmt.setString(10, v.getModel());
            pstmt.setString(11, v.getMake());
            pstmt.setInt(12, v.getManufacturingYear());
            pstmt.setInt(13, v.getMileage());
            pstmt.setDouble(14, v.getPricePerDay());
            pstmt.setInt(15, v.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int id) {
        String sql = "UPDATE vehicles SET is_active = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setId(rs.getInt("id"));
        v.setLocationId(rs.getInt("location_id"));
        v.setParkingStallId((Integer) rs.getObject("parking_stall_id"));
        v.setBarcodeId(rs.getInt("barcode_id"));
        v.setVehicleType(VehicleType.fromInt(rs.getInt("vehicle_type")));
        v.setCarType(CarType.fromInt(rs.getInt("car_type")));
        v.setLicenseNumber(rs.getString("license_number"));
        v.setStockNumber(rs.getString("stock_number"));
        v.setPassengerCapacity(rs.getInt("passenger_capacity"));
        v.setHasSunroof(rs.getBoolean("has_sunroof"));
        v.setStatus(VehicleStatus.fromInt(rs.getInt("status")));
        v.setModel(rs.getString("model"));
        v.setMake(rs.getString("make"));
        v.setManufacturingYear(rs.getInt("manufacturing_year"));
        v.setMileage(rs.getInt("mileage"));
        v.setPricePerDay(rs.getDouble("price_per_day"));
        v.setBarcode(rs.getString("barcode"));
        v.setActive(rs.getBoolean("is_active"));
        try { v.setLocationName(rs.getString("loc_name")); } catch (Exception e) {}
        try { v.setSystemName(rs.getString("sys_name")); } catch (Exception e) {}
        return v;
    }

    public List<String> getVehicleHistory(int vehicleId) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT r.reservation_number, p.name as member_name, r.creation_date as picked_up, " +
                     "r.return_date, staff_p.name as staff_name FROM vehicle_reservations r " +
                     "JOIN members m ON r.member_id = m.id " +
                     "JOIN accounts a ON m.account_id = a.id " +
                     "JOIN persons p ON a.person_id = p.id " +
                     "LEFT JOIN accounts staff_a ON r.processed_by_account_id = staff_a.id " +
                     "LEFT JOIN persons staff_p ON staff_a.person_id = staff_p.id " +
                     "WHERE r.vehicle_id = ? ORDER BY r.creation_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String staff = rs.getString("staff_name") != null ? rs.getString("staff_name") : "N/A";
                    String retDate = rs.getTimestamp("return_date") != null ? rs.getTimestamp("return_date").toString() : "Not returned yet";
                    history.add(String.format("[%s] Member: %s | Pickup: %s | Return: %s | Staff: %s", 
                        rs.getString("reservation_number"), rs.getString("member_name"), 
                        rs.getTimestamp("picked_up"), retDate, staff));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return history;
    }

    public List<com.cs210.project.models.VehicleReservation> getVehicleHistoryObjects(int vehicleId) {
        List<com.cs210.project.models.VehicleReservation> history = new ArrayList<>();
        String sql = "SELECT r.*, p.name as member_name, staff_p.name as staff_name, b.total_amount " +
                     "FROM vehicle_reservations r " +
                     "JOIN members m ON r.member_id = m.id " +
                     "JOIN accounts a ON m.account_id = a.id " +
                     "JOIN persons p ON a.person_id = p.id " +
                     "LEFT JOIN bills b ON b.reservation_id = r.id " +
                     "LEFT JOIN accounts staff_a ON r.processed_by_account_id = staff_a.id " +
                     "LEFT JOIN persons staff_p ON staff_a.person_id = staff_p.id " +
                     "WHERE r.vehicle_id = ? ORDER BY r.creation_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    com.cs210.project.models.VehicleReservation res = new com.cs210.project.models.VehicleReservation();
                    res.setReservationNumber(rs.getString("reservation_number"));
                    res.setMemberName(rs.getString("member_name"));
                    res.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
                    if (rs.getTimestamp("return_date") != null) {
                        res.setReturnDate(rs.getTimestamp("return_date").toLocalDateTime());
                    }
                    res.setStaffName(rs.getString("staff_name") != null ? rs.getString("staff_name") : "N/A");
                    res.setAmount(rs.getDouble("total_amount"));
                    res.setStatus(com.cs210.project.constants.Enums.ReservationStatus.fromInt(rs.getInt("status")));
                    history.add(res);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return history;
    }

    public double getVehicleProfit(int vehicleId) {
        String sql = "SELECT SUM(p.amount) as total_profit FROM payments p " +
                     "JOIN bills b ON p.bill_id = b.id " +
                     "JOIN vehicle_reservations r ON b.reservation_id = r.id " +
                     "WHERE r.vehicle_id = ? AND p.status = 1"; // Status 1 = COMPLETED
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("total_profit");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public void reactivate(int id) {
        String sql = "UPDATE vehicles SET is_active = TRUE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
