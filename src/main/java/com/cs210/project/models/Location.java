package com.cs210.project.models;

public class Location {
    private int id;
    private int systemId;
    private String name;
    private String streetAddress;
    private String city;
    private String state;
    private String zipcode;
    private String country;

    public Location() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSystemId() { return systemId; }
    public void setSystemId(int systemId) { this.systemId = systemId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getZipcode() { return zipcode; }
    public void setZipcode(String zipcode) { this.zipcode = zipcode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    @Override
    public String toString() {
        return name;
    }
}
