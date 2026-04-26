package com.cs210.project.services;

import com.cs210.project.constants.Enums.PaymentType;
import com.cs210.project.models.Bill;
import com.cs210.project.repositories.BillRepository;
import com.cs210.project.repositories.PaymentRepository;
import com.cs210.project.repositories.NotificationRepository;
import com.cs210.project.constants.Enums.NotificationType;

import java.math.BigDecimal;

public class PaymentService {
    private final BillRepository billRepo = new BillRepository();
    private final PaymentRepository payRepo = new PaymentRepository();
    private final NotificationRepository notifyRepo = new NotificationRepository();

    public void payBill(int reservationId, BigDecimal amount, PaymentType type) throws Exception {
        Bill bill = billRepo.findByReservationId(reservationId);
        if (bill == null) throw new Exception("Bill not found.");

        payRepo.processPayment(bill.getId(), amount, type);
        notifyRepo.create(reservationId, NotificationType.SYSTEM, "Payment of $" + amount + " received via " + type.getLabel());
    }

    public Bill getBill(int reservationId) {
        return billRepo.findByReservationId(reservationId);
    }
}
