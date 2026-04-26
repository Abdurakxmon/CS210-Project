package com.cs210.project.models;

public class ParkingStall {
    private int id;
    private int locationId;
    private String stallNumber;
    private String locationIdentifier;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }
    public String getStallNumber() { return stallNumber; }
    public void setStallNumber(String stallNumber) { this.stallNumber = stallNumber; }
    public String getLocationIdentifier() { return locationIdentifier; }
    public void setLocationIdentifier(String locationIdentifier) { this.locationIdentifier = locationIdentifier; }

    @Override
    public String toString() {
        return stallNumber + (locationIdentifier != null ? " (" + locationIdentifier + ")" : "");
    }
}
