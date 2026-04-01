package com.carrental.api.service.impl;

import com.carrental.api.entity.Notification;
import com.carrental.api.service.NotificationService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public List<Notification> getNotificationsByReservationId(Long reservationId) {
        throw new UnsupportedOperationException("Notification lookup is not implemented yet");
    }
}
