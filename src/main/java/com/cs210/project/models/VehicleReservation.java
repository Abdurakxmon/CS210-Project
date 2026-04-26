package com.cs210.project.models;

import com.cs210.project.constants.Enums.ReservationStatus;
import java.time.LocalDateTime;

public class VehicleReservation {
    private int id;
    private String reservationNumber;
    private int memberId;
    private int vehicleId;
    private LocalDateTime creationDate;
    private ReservationStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private int pickupLocationId;
    private int returnLocationId;

    // Transient fields for UI display
    private String vehicleMake;
    private String vehicleModel;
    private String vehiclePlate;
    private String memberName;
    private String staffName;
    private double amount;

    public VehicleReservation() {}

    // Getters and Setters
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }
    public int getPickupLocationId() { return pickupLocationId; }
    public void setPickupLocationId(int pickupLocationId) { this.pickupLocationId = pickupLocationId; }
    public int getReturnLocationId() { return returnLocationId; }
    public void setReturnLocationId(int returnLocationId) { this.returnLocationId = returnLocationId; }

    public String getVehicleMake() { return vehicleMake; }
    public void setVehicleMake(String vehicleMake) { this.vehicleMake = vehicleMake; }
    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    public String getVehiclePlate() { return vehiclePlate; }
    public void setVehiclePlate(String vehiclePlate) { this.vehiclePlate = vehiclePlate; }
}
