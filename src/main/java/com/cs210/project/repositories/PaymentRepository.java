package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.PaymentStatus;
import com.cs210.project.constants.Enums.PaymentType;
import com.cs210.project.models.Payment;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {

    public void processPayment(int billId, BigDecimal amount, PaymentType type) {
        processPayment(billId, amount, type, null);
    }

    public void processPayment(int billId, BigDecimal amount, PaymentType type, Integer processedByAccountId) {
        String sql = "INSERT INTO payments (bill_id, creation_date, amount, status, payment_type, processed_by_account_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, billId);
            pstmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            pstmt.setBigDecimal(3, amount);
            pstmt.setInt(4, PaymentStatus.COMPLETED.getValue());
            pstmt.setInt(5, type.getValue());
            if (processedByAccountId != null) pstmt.setInt(6, processedByAccountId); else pstmt.setNull(6, Types.INTEGER);
            pstmt.executeUpdate();
            
            int paymentId;
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) paymentId = rs.getInt(1); else return;
            }
            
            if (type == PaymentType.CASH) {
                insertCashTransaction(paymentId, amount);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void insertCashTransaction(int paymentId, BigDecimal amount) {
        String sql = "INSERT INTO cash_transactions (payment_id, cash_tendered) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, paymentId);
            pstmt.setBigDecimal(2, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Payment> findByBillId(int billId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE bill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, billId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Payment p = new Payment();
                    p.setId(rs.getInt("id"));
                    p.setBillId(rs.getInt("bill_id"));
                    p.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
                    p.setAmount(rs.getBigDecimal("amount"));
                    p.setStatus(PaymentStatus.fromInt(rs.getInt("status")));
                    p.setPaymentType(PaymentType.fromInt(rs.getInt("payment_type")));
                    list.add(p);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public BigDecimal getSuccessfulPaidAmount(int billId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE bill_id = ? AND status IN (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, billId);
            pstmt.setInt(2, PaymentStatus.COMPLETED.getValue());
            pstmt.setInt(3, PaymentStatus.SETTLED.getValue());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return BigDecimal.ZERO;
    }

    public boolean hasFailedPayment(int billId) {
        String sql = "SELECT COUNT(*) FROM payments WHERE bill_id = ? AND status IN (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, billId);
            pstmt.setInt(2, PaymentStatus.FAILED.getValue());
            pstmt.setInt(3, PaymentStatus.DECLINED.getValue());
            pstmt.setInt(4, PaymentStatus.CANCELLED.getValue());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Payment> findByMemberId(int memberId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, r.reservation_number, v.make, v.model FROM payments p " +
                     "JOIN bills b ON p.bill_id = b.id " +
                     "JOIN vehicle_reservations r ON b.reservation_id = r.id " +
                     "JOIN vehicles v ON r.vehicle_id = v.id " +
                     "WHERE r.member_id = ? ORDER BY p.creation_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Payment p = new Payment();
                    p.setId(rs.getInt("id"));
                    p.setBillId(rs.getInt("bill_id"));
                    p.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
                    p.setAmount(rs.getBigDecimal("amount"));
                    p.setStatus(PaymentStatus.fromInt(rs.getInt("status")));
                    p.setPaymentType(PaymentType.fromInt(rs.getInt("payment_type")));
                    p.setReservationNumber(rs.getString("reservation_number"));
                    p.setVehicleName(rs.getString("make") + " " + rs.getString("model"));
                    list.add(p);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
