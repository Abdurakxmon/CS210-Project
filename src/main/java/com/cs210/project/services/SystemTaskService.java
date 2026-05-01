package com.cs210.project.services;

import com.cs210.project.config.DatabaseConnection;
import com.cs210.project.constants.Enums.BillItemType;
import com.cs210.project.constants.Enums.NotificationType;
import com.cs210.project.constants.Enums.ReservationStatus;
import com.cs210.project.constants.Enums.VehicleLogType;
import com.cs210.project.models.Bill;
import com.cs210.project.models.VehicleReservation;
import com.cs210.project.repositories.BillRepository;
import com.cs210.project.repositories.NotificationRepository;
import com.cs210.project.repositories.ReservationRepository;
import com.cs210.project.repositories.VehicleLogRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SystemTaskService {
    private final ReservationRepository reservationRepo = new ReservationRepository();
    private final NotificationRepository notificationRepo = new NotificationRepository();
    private final BillRepository billRepo = new BillRepository();
    private final VehicleLogRepository logRepo = new VehicleLogRepository();

    public void runSystemTasks() {
        for (VehicleReservation reservation : reservationRepo.findAll()) {
            handleReminders(reservation);
            handleOverdue(reservation);
        }
    }

    private void handleReminders(VehicleReservation reservation) {
        LocalDateTime now = LocalDateTime.now();
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            long hoursUntilPickup = ChronoUnit.HOURS.between(now, reservation.getPickupDate());
            if (hoursUntilPickup <= 24 && hoursUntilPickup >= 0 &&
                    !notificationRepo.exists(reservation.getId(), NotificationType.PICKUP_REMINDER)) {
                notificationRepo.create(reservation.getId(), NotificationType.PICKUP_REMINDER,
                        "Pickup reminder for reservation " + reservation.getReservationNumber() + ".");
            }
            if (hoursUntilPickup <= 48 && hoursUntilPickup >= 0 &&
                    !notificationRepo.exists(reservation.getId(), NotificationType.RESERVATION_REMINDER)) {
                notificationRepo.create(reservation.getId(), NotificationType.RESERVATION_REMINDER,
                        "Upcoming reservation reminder for " + reservation.getReservationNumber() + ".");
            }
        }

        if (reservation.getStatus() == ReservationStatus.PENDING) {
            long hoursUntilDue = ChronoUnit.HOURS.between(now, reservation.getDueDate());
            if (hoursUntilDue <= 24 && hoursUntilDue >= 0 &&
                    !notificationRepo.exists(reservation.getId(), NotificationType.DUE_DATE_REMINDER)) {
                notificationRepo.create(reservation.getId(), NotificationType.DUE_DATE_REMINDER,
                        "Due-date reminder for reservation " + reservation.getReservationNumber() + ".");
            }
        }
    }

    private void handleOverdue(VehicleReservation reservation) {
        if (reservation.getStatus() != ReservationStatus.PENDING || !LocalDateTime.now().isAfter(reservation.getDueDate())) {
            return;
        }

        updateReservationStatus(reservation.getId(), ReservationStatus.OVERDUE);
        if (!notificationRepo.exists(reservation.getId(), NotificationType.OVERDUE_WARNING)) {
            notificationRepo.create(reservation.getId(), NotificationType.OVERDUE_WARNING,
                    "Reservation " + reservation.getReservationNumber() + " is overdue.");
        }

        Bill bill = billRepo.findByReservationId(reservation.getId());
        if (bill != null && !billRepo.hasItem(bill.getId(), BillItemType.LATE_FEE, "Overdue Late Fee")) {
            billRepo.addItem(bill.getId(), BillItemType.LATE_FEE, new BigDecimal("25.00"), "Overdue Late Fee");
            billRepo.updateTotal(bill.getId());
            notificationRepo.create(reservation.getId(), NotificationType.LATE_FEE_ADDED,
                    "A late fee has been added to reservation " + reservation.getReservationNumber() + ".");
        }

        logRepo.addLog(reservation.getVehicleId(), VehicleLogType.OTHER,
                "Reservation " + reservation.getReservationNumber() + " marked overdue by system task.", null);
    }

    private void updateReservationStatus(int reservationId, ReservationStatus status) {
        String sql = "UPDATE vehicle_reservations SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, status.getValue());
            pstmt.setInt(2, reservationId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
