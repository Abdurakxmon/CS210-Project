package com.cs210.project.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Bill {
    private int id;
    private int reservationId;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private List<BillItem> items = new ArrayList<>();

    public Bill() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }
}
