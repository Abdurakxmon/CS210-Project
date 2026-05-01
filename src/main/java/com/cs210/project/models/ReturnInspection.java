package com.cs210.project.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReturnInspection {
    private int id;
    private int reservationId;
    private int vehicleId;
    private int workerAccountId;
    private LocalDateTime inspectionDate;
    private int mileage;
    private int fuelLevel;
    private String damageDescription;
    private BigDecimal damageFee = BigDecimal.ZERO;
    private BigDecimal fuelFee = BigDecimal.ZERO;
    private boolean cleaned;
    private boolean maintenanceRequired;
    private Integer parkingStallId;
    private String notes;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public int getWorkerAccountId() { return workerAccountId; }
    public void setWorkerAccountId(int workerAccountId) { this.workerAccountId = workerAccountId; }
    public LocalDateTime getInspectionDate() { return inspectionDate; }
    public void setInspectionDate(LocalDateTime inspectionDate) { this.inspectionDate = inspectionDate; }
    public int getMileage() { return mileage; }
    public void setMileage(int mileage) { this.mileage = mileage; }
    public int getFuelLevel() { return fuelLevel; }
    public void setFuelLevel(int fuelLevel) { this.fuelLevel = fuelLevel; }
    public String getDamageDescription() { return damageDescription; }
    public void setDamageDescription(String damageDescription) { this.damageDescription = damageDescription; }
    public BigDecimal getDamageFee() { return damageFee; }
    public void setDamageFee(BigDecimal damageFee) { this.damageFee = damageFee; }
    public BigDecimal getFuelFee() { return fuelFee; }
    public void setFuelFee(BigDecimal fuelFee) { this.fuelFee = fuelFee; }
    public boolean isCleaned() { return cleaned; }
    public void setCleaned(boolean cleaned) { this.cleaned = cleaned; }
    public boolean isMaintenanceRequired() { return maintenanceRequired; }
    public void setMaintenanceRequired(boolean maintenanceRequired) { this.maintenanceRequired = maintenanceRequired; }
    public Integer getParkingStallId() { return parkingStallId; }
    public void setParkingStallId(Integer parkingStallId) { this.parkingStallId = parkingStallId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
