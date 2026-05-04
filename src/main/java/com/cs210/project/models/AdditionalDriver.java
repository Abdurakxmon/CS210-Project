package com.cs210.project.models;

public class AdditionalDriver {
    private int id;
    private int reservationId;
    private int personId;
    private String driverId; // Driving License Number

    public AdditionalDriver() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public int getPersonId() { return personId; }
    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
}
