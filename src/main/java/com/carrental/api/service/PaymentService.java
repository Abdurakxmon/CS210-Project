package com.carrental.api.service;

import com.carrental.api.dto.request.CreatePaymentRequest;
import com.carrental.api.entity.Payment;
import java.util.List;

public interface PaymentService {

    Payment createPayment(CreatePaymentRequest request);

    List<Payment> getAllPayments();

    Payment getPaymentById(Long id);
}
