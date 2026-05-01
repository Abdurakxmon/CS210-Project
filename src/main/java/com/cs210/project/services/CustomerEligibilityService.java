package com.cs210.project.services;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.AccountStatus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

public class CustomerEligibilityService {
    public static final int MINIMUM_RENTAL_AGE = 18;

    public void validateCustomerEligibility(int memberId) throws Exception {
        String sql = "SELECT a.status, a.is_active, m.driver_license_expiry, p.birth_date " +
                "FROM members m JOIN accounts a ON m.account_id = a.id JOIN persons p ON a.person_id = p.id " +
                "WHERE m.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) throw new Exception("Customer account not found.");
                if (!rs.getBoolean("is_active")) throw new Exception("Customer account is inactive.");
                AccountStatus status = AccountStatus.fromInt(rs.getInt("status"));
                if (status == AccountStatus.BLACKLISTED || status == AccountStatus.CLOSED || status == AccountStatus.CANCELLED) {
                    throw new Exception("Customer account status does not allow reservations: " + status.getLabel());
                }
                if (rs.getTimestamp("driver_license_expiry") == null ||
                        rs.getTimestamp("driver_license_expiry").toLocalDateTime().isBefore(java.time.LocalDateTime.now())) {
                    throw new Exception("Driving license is expired.");
                }
                LocalDate birthDate = rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null;
                if (birthDate == null || Period.between(birthDate, LocalDate.now()).getYears() < MINIMUM_RENTAL_AGE) {
                    throw new Exception("Customer must be at least " + MINIMUM_RENTAL_AGE + " years old.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Could not validate customer eligibility.", e);
        }

        if (hasUnpaidPriorBills(memberId)) {
            throw new Exception("Customer has unpaid previous bills.");
        }
    }

    public void validateNoFailedPaymentForReservation(int reservationId) throws Exception {
        String sql = "SELECT COUNT(*) FROM payments p JOIN bills b ON p.bill_id = b.id " +
                "WHERE b.reservation_id = ? AND p.status IN (4, 5, 6)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new Exception("Reservation has a failed or cancelled required payment.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Could not validate reservation payments.", e);
        }
    }

    private boolean hasUnpaidPriorBills(int memberId) throws SQLException {
        String sql = "SELECT b.total_amount, COALESCE(SUM(CASE WHEN p.status IN (3, 9) THEN p.amount ELSE 0 END), 0) AS paid " +
                "FROM vehicle_reservations r JOIN bills b ON b.reservation_id = r.id " +
                "LEFT JOIN payments p ON p.bill_id = b.id " +
                "WHERE r.member_id = ? AND r.status IN (4, 5, 8) " +
                "GROUP BY b.id, b.total_amount HAVING b.total_amount > paid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total_amount");
                    BigDecimal paid = rs.getBigDecimal("paid");
                    return total != null && paid != null && total.compareTo(paid) > 0;
                }
            }
        }
        return false;
    }
}
