package com.cs210.project.models;

import com.cs210.project.constants.Enums.VehicleLogType;
import java.time.LocalDateTime;

public class VehicleLog {
    private int id;
    private int vehicleId;
    private VehicleLogType logType;
    private String description;
    private LocalDateTime creationDate;
    private Integer accountId; // Staff who created the log

    public VehicleLog() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public VehicleLogType getLogType() { return logType; }
    public void setLogType(VehicleLogType logType) { this.logType = logType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }
}
