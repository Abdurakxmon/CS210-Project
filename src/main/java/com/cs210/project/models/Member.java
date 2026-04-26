package com.cs210.project.models;

import java.time.LocalDateTime;

public class Member {
    private int id;
    private int accountId;
    private String driverLicenseNumber;
    private LocalDateTime driverLicenseExpiry;

    public Member() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    public String getDriverLicenseNumber() { return driverLicenseNumber; }
    public void setDriverLicenseNumber(String driverLicenseNumber) { this.driverLicenseNumber = driverLicenseNumber; }
    public LocalDateTime getDriverLicenseExpiry() { return driverLicenseExpiry; }
    public void setDriverLicenseExpiry(LocalDateTime driverLicenseExpiry) { this.driverLicenseExpiry = driverLicenseExpiry; }
}
