package com.cs210.project.models;

public class ParkingStall {
    private int id;
    private int locationId;
    private String stallNumber;
    private String locationIdentifier;
    private String locationName;
    private String assignedVehicleName;
    private String assignedVehiclePlate;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }
    public String getStallNumber() { return stallNumber; }
    public void setStallNumber(String stallNumber) { this.stallNumber = stallNumber; }
    public String getLocationIdentifier() { return locationIdentifier; }
    public void setLocationIdentifier(String locationIdentifier) { this.locationIdentifier = locationIdentifier; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public String getAssignedVehicleName() { return assignedVehicleName; }
    public void setAssignedVehicleName(String assignedVehicleName) { this.assignedVehicleName = assignedVehicleName; }
    public String getAssignedVehiclePlate() { return assignedVehiclePlate; }
    public void setAssignedVehiclePlate(String assignedVehiclePlate) { this.assignedVehiclePlate = assignedVehiclePlate; }
    public String getOccupancyStatus() { return assignedVehicleName == null || assignedVehicleName.isBlank() ? "Available" : "Occupied"; }
    public String getAssignedVehicleDisplay() {
        if (assignedVehicleName == null || assignedVehicleName.isBlank()) return "None";
        return assignedVehicleName + (assignedVehiclePlate != null && !assignedVehiclePlate.isBlank() ? " (" + assignedVehiclePlate + ")" : "");
    }

    @Override
    public String toString() {
        return stallNumber + (locationIdentifier != null && !locationIdentifier.isBlank() ? " (" + locationIdentifier + ")" : "");
    }
}
