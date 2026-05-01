package com.cs210.project.repositories;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.NotificationType;
import com.cs210.project.models.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    public void create(int reservationId, NotificationType type, String content) {
        String sql = "INSERT INTO notifications (reservation_id, notification_type, created_on, content) VALUES (?, ?, ?, ?)";
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

    public List<Notification> findByMemberId(int memberId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT n.* FROM notifications n " +
                     "JOIN vehicle_reservations r ON n.reservation_id = r.id " +
                     "WHERE r.member_id = ? ORDER BY n.created_on DESC";
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
                    list.add(n);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
