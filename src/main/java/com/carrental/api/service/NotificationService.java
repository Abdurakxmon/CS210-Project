package com.carrental.api.service;

import com.carrental.api.entity.Notification;
import java.util.List;

public interface NotificationService {

    List<Notification> getNotificationsByReservationId(Long reservationId);
}
