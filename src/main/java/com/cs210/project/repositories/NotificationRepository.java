package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.NotificationType;
import com.cs210.project.models.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {
    private static boolean readColumnChecked = false;

    public void create(int reservationId, NotificationType type, String content) {
        ensureReadColumn();
        String sql = "INSERT INTO notifications (reservation_id, notification_type, created_on, content, is_read) VALUES (?, ?, ?, ?, FALSE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            pstmt.setInt(2, type.getValue());
            pstmt.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            pstmt.setString(4, content);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean exists(int reservationId, NotificationType type) {
        ensureReadColumn();
        String sql = "SELECT COUNT(*) FROM notifications WHERE reservation_id = ? AND notification_type = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            pstmt.setInt(2, type.getValue());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public int countByMemberId(int memberId) {
        return countUnreadByMemberId(memberId);
    }

    public int countUnreadByMemberId(int memberId) {
        ensureReadColumn();
        String sql = "SELECT COUNT(*) FROM notifications n " +
                     "JOIN vehicle_reservations r ON n.reservation_id = r.id " +
                     "WHERE r.member_id = ? AND n.is_read = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public void markByMemberIdAsRead(int memberId) {
        ensureReadColumn();
        String sql = "UPDATE notifications n " +
                     "JOIN vehicle_reservations r ON n.reservation_id = r.id " +
                     "SET n.is_read = TRUE " +
                     "WHERE r.member_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Notification> findByMemberId(int memberId) {
        ensureReadColumn();
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT n.* FROM notifications n " +
                     "JOIN vehicle_reservations r ON n.reservation_id = r.id " +
                     "WHERE r.member_id = ? ORDER BY n.is_read ASC, n.created_on DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification();
                    n.setId(rs.getInt("id"));
                    n.setReservationId(rs.getInt("reservation_id"));
                    n.setNotificationType(NotificationType.fromInt(rs.getInt("notification_type")));
                    n.setCreatedOn(rs.getTimestamp("created_on").toLocalDateTime());
                    n.setContent(rs.getString("content"));
                    n.setRead(rs.getBoolean("is_read"));
                    list.add(n);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private void ensureReadColumn() {
        if (readColumnChecked) {
            return;
        }
        String sql = "ALTER TABLE notifications ADD COLUMN is_read BOOLEAN NOT NULL DEFAULT FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (!message.contains("duplicate") && !message.contains("exists")) {
                e.printStackTrace();
            }
        } finally {
            readColumnChecked = true;
        }
    }
}
