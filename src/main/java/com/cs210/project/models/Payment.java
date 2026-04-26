package com.cs210.project.models;

import com.cs210.project.constants.Enums.PaymentStatus;
import com.cs210.project.constants.Enums.PaymentType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private int id;
    private int billId;
    private LocalDateTime creationDate;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentType paymentType;

    public Payment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
}
