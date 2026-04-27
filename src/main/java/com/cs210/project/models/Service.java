package com.cs210.project.models;

import com.cs210.project.constants.Enums.ServiceType;
import java.math.BigDecimal;

public class Service {
    private int id;
    private int reservationId;
    private ServiceType serviceType;
    private BigDecimal price;

    public Service() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
