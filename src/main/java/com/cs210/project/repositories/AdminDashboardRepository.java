package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.models.AdminDashboardStats;
import com.cs210.project.models.AdminMetricRow;
import com.cs210.project.models.AuditLogEntry;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardRepository {
    public AdminDashboardStats getStats() {
        AdminDashboardStats stats = new AdminDashboardStats();
        stats.setTotalVehicles(intValue("SELECT COUNT(*) FROM vehicles WHERE is_active = TRUE"));
        stats.setActiveReservations(intValue("SELECT COUNT(*) FROM vehicle_reservations WHERE status = 2"));
        stats.setPendingInspections(intValue("SELECT COUNT(*) FROM vehicle_reservations WHERE status = 7"));
        stats.setCompletedReservations(intValue("SELECT COUNT(*) FROM vehicle_reservations WHERE status = 4"));
        stats.setRevenue(decimalValue("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE status IN (3, 9)"));
        stats.setOutstanding(decimalValue(
                "SELECT COALESCE(SUM(GREATEST(b.total_amount - COALESCE(paid.amount, 0), 0)), 0) " +
                "FROM bills b LEFT JOIN (SELECT bill_id, SUM(amount) amount FROM payments WHERE status IN (3, 9) GROUP BY bill_id) paid ON paid.bill_id = b.id"));
        return stats;
    }

    public List<AdminMetricRow> getMostPickedVehicles() {
        String sql = "SELECT CONCAT(v.make, ' ', v.model) name, v.license_number detail, COUNT(r.id) cnt, " +
                "COALESCE(SUM(b.total_amount), 0) amount FROM vehicles v " +
                "LEFT JOIN vehicle_reservations r ON r.vehicle_id = v.id " +
                "LEFT JOIN bills b ON b.reservation_id = r.id " +
                "GROUP BY v.id, v.make, v.model, v.license_number ORDER BY cnt DESC, amount DESC LIMIT 5";
        return metricRows(sql, true);
    }

    public List<AdminMetricRow> getBestWorkers() {
        String sql = "SELECT p.name name, a.username detail, COUNT(ri.id) cnt, " +
                "COALESCE(SUM(ri.damage_fee + ri.fuel_fee), 0) amount FROM accounts a " +
                "JOIN persons p ON a.person_id = p.id " +
                "LEFT JOIN return_inspections ri ON ri.worker_account_id = a.id " +
                "WHERE a.role_type = 3 GROUP BY a.id, p.name, a.username ORDER BY cnt DESC, amount DESC LIMIT 5";
        return metricRows(sql, true);
    }

    public List<AdminMetricRow> getTopPaymentStaff() {
        String sql = "SELECT COALESCE(p.name, 'Member self-service') name, COALESCE(a.username, 'online') detail, " +
                "COUNT(pay.id) cnt, COALESCE(SUM(pay.amount), 0) amount FROM payments pay " +
                "LEFT JOIN accounts a ON pay.processed_by_account_id = a.id " +
                "LEFT JOIN persons p ON a.person_id = p.id " +
                "WHERE pay.status IN (3, 9) GROUP BY pay.processed_by_account_id, p.name, a.username " +
                "ORDER BY amount DESC, cnt DESC LIMIT 5";
        return metricRows(sql, true);
    }

    public List<AuditLogEntry> getAuditLog() {
        List<AuditLogEntry> rows = new ArrayList<>();
        String sql =
                "SELECT vl.creation_date event_time, 'Vehicle Log' category, COALESCE(p.name, 'System') actor, " +
                "CONCAT(v.make, ' ', v.model, ' ', v.license_number) subject, vl.description description " +
                "FROM vehicle_logs vl JOIN vehicles v ON vl.vehicle_id = v.id " +
                "LEFT JOIN accounts a ON vl.account_id = a.id LEFT JOIN persons p ON a.person_id = p.id " +
                "UNION ALL " +
                "SELECT pay.creation_date event_time, 'Payment' category, COALESCE(p.name, 'Member self-service') actor, " +
                "r.reservation_number subject, CONCAT('Accepted $', pay.amount, ' via payment type ', pay.payment_type) description " +
                "FROM payments pay JOIN bills b ON pay.bill_id = b.id JOIN vehicle_reservations r ON b.reservation_id = r.id " +
                "LEFT JOIN accounts a ON pay.processed_by_account_id = a.id LEFT JOIN persons p ON a.person_id = p.id " +
                "UNION ALL " +
                "SELECT ri.inspection_date event_time, 'Inspection' category, COALESCE(p.name, 'Worker') actor, " +
                "r.reservation_number subject, CONCAT('Mileage ', ri.mileage, ', fuel ', ri.fuel_level, '%, damage fee $', ri.damage_fee, ', fuel fee $', ri.fuel_fee) description " +
                "FROM return_inspections ri JOIN vehicle_reservations r ON ri.reservation_id = r.id " +
                "LEFT JOIN accounts a ON ri.worker_account_id = a.id LEFT JOIN persons p ON a.person_id = p.id " +
                "ORDER BY event_time DESC LIMIT 200";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new AuditLogEntry(
                        toLocalDateTime(rs),
                        rs.getString("category"),
                        rs.getString("actor"),
                        rs.getString("subject"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    private int intValue(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private BigDecimal decimalValue(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    private List<AdminMetricRow> metricRows(String sql, boolean money) {
        List<AdminMetricRow> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                BigDecimal amount = rs.getBigDecimal("amount");
                rows.add(new AdminMetricRow(
                        rs.getString("name"),
                        rs.getString("detail"),
                        rs.getInt("cnt"),
                        money ? "$" + String.format("%.2f", amount == null ? BigDecimal.ZERO : amount) : ""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    private LocalDateTime toLocalDateTime(ResultSet rs) throws SQLException {
        return rs.getTimestamp("event_time") == null ? null : rs.getTimestamp("event_time").toLocalDateTime();
    }
}
