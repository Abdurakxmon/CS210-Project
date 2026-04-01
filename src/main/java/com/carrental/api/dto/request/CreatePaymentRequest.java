package com.carrental.api.dto.request;

import com.carrental.api.entity.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreatePaymentRequest(
    @NotNull Long billId,
    @NotNull @DecimalMin("0.0") BigDecimal amount,
    @NotNull PaymentStatus status,
    @NotBlank String paymentType,
    String nameOnCard,
    String bankName,
    String checkNumber,
    BigDecimal cashTendered
) {
}
