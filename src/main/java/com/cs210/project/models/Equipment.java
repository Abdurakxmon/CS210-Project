package com.cs210.project.models;

import com.cs210.project.constants.Enums.EquipmentType;
import java.math.BigDecimal;

public class Equipment {
    private int id;
    private int reservationId;
    private EquipmentType equipmentType;
    private BigDecimal price;

    public Equipment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public EquipmentType getEquipmentType() { return equipmentType; }
    public void setEquipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
