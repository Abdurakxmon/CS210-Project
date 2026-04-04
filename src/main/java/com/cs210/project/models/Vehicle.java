package com.cs210.project.models;

import java.sql.Timestamp;

public class Vehicle {
    private long id;
    private Long inventoryId; // nullable
    private long locationId;
    private Long parkingStallId; // nullable
    private long barcodeId;

    private String vehicleType;
    private String category; // nullable
    private String licenseNumber;
    private String stockNumber;
    private Integer passengerCapacity; // nullable
    private Boolean hasSunroof; // nullable
    private String status;
    private String model; // nullable
    private String make; // nullable
    private Integer manufacturingYear; // nullable
    private Integer mileage; // nullable
    
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Vehicle() {}

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Long getInventoryId() { return inventoryId; }
    public void setInventoryId(Long inventoryId) { this.inventoryId = inventoryId; }

    public long getLocationId() { return locationId; }
    public void setLocationId(long locationId) { this.locationId = locationId; }

    public Long getParkingStallId() { return parkingStallId; }
    public void setParkingStallId(Long parkingStallId) { this.parkingStallId = parkingStallId; }

    public long getBarcodeId() { return barcodeId; }
    public void setBarcodeId(long barcodeId) { this.barcodeId = barcodeId; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getStockNumber() { return stockNumber; }
    public void setStockNumber(String stockNumber) { this.stockNumber = stockNumber; }

    public Integer getPassengerCapacity() { return passengerCapacity; }
    public void setPassengerCapacity(Integer passengerCapacity) { this.passengerCapacity = passengerCapacity; }

    public Boolean getHasSunroof() { return hasSunroof; }
    public void setHasSunroof(Boolean hasSunroof) { this.hasSunroof = hasSunroof; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public Integer getManufacturingYear() { return manufacturingYear; }
    public void setManufacturingYear(Integer manufacturingYear) { this.manufacturingYear = manufacturingYear; }

    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", vehicleType='" + vehicleType + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
