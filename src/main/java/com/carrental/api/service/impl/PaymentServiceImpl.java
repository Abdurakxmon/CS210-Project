package com.carrental.api.service.impl;

import com.carrental.api.dto.request.CreatePaymentRequest;
import com.carrental.api.entity.Payment;
import com.carrental.api.service.PaymentService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public Payment createPayment(CreatePaymentRequest request) {
        throw new UnsupportedOperationException("Payment creation is not implemented yet");
    }

    @Override
    public List<Payment> getAllPayments() {
        throw new UnsupportedOperationException("Payment lookup is not implemented yet");
    }

    @Override
    public Payment getPaymentById(Long id) {
        throw new UnsupportedOperationException("Payment lookup is not implemented yet");
    }
}
