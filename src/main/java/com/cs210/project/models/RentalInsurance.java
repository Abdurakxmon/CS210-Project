package com.cs210.project.models;

import com.cs210.project.constants.Enums.InsuranceType;
import java.math.BigDecimal;

public class RentalInsurance {
    private int id;
    private int reservationId;
    private InsuranceType insuranceType;
    private BigDecimal price;

    public RentalInsurance() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public InsuranceType getInsuranceType() { return insuranceType; }
    public void setInsuranceType(InsuranceType insuranceType) { this.insuranceType = insuranceType; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
