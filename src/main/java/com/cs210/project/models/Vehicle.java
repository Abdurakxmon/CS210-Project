package com.cs210.project.models;

import com.cs210.project.constants.Enums.CarType;
import com.cs210.project.constants.Enums.VehicleType;
import com.cs210.project.constants.VehicleStatus;

public class Vehicle {
    private int id;
    private int locationId;
    private Integer parkingStallId;
    private int barcodeId;
    private VehicleType vehicleType;
    private CarType carType;
    private String licenseNumber;
    private String stockNumber;
    private int passengerCapacity;
    private boolean hasSunroof;
    private VehicleStatus status;
    private String model;
    private String make;
    private int manufacturingYear;
    private int mileage;
    private double pricePerDay;

    // Associated objects
    private String barcode; // For display convenience
    private String locationName;
    private String systemName;
    private boolean isActive;

    public Vehicle() {}

    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }

    // Getters and Setters
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }
    public Integer getParkingStallId() { return parkingStallId; }
    public void setParkingStallId(Integer parkingStallId) { this.parkingStallId = parkingStallId; }
    public int getBarcodeId() { return barcodeId; }
    public void setBarcodeId(int barcodeId) { this.barcodeId = barcodeId; }
    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
    public CarType getCarType() { return carType; }
    public void setCarType(CarType carType) { this.carType = carType; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public String getStockNumber() { return stockNumber; }
    public void setStockNumber(String stockNumber) { this.stockNumber = stockNumber; }
    public int getPassengerCapacity() { return passengerCapacity; }
    public void setPassengerCapacity(int passengerCapacity) { this.passengerCapacity = passengerCapacity; }
    public boolean isHasSunroof() { return hasSunroof; }
    public void setHasSunroof(boolean hasSunroof) { this.hasSunroof = hasSunroof; }
    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public int getManufacturingYear() { return manufacturingYear; }
    public void setManufacturingYear(int manufacturingYear) { this.manufacturingYear = manufacturingYear; }
    public int getMileage() { return mileage; }
    public void setMileage(int mileage) { this.mileage = mileage; }
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    
    @Override
    public String toString() {
        return make + " " + model + " (" + licenseNumber + ")";
    }
}
