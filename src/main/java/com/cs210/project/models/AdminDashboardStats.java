package com.cs210.project.models;

import java.math.BigDecimal;

public class AdminDashboardStats {
    private int totalVehicles;
    private int activeReservations;
    private int pendingInspections;
    private int completedReservations;
    private BigDecimal revenue = BigDecimal.ZERO;
    private BigDecimal outstanding = BigDecimal.ZERO;

    public int getTotalVehicles() { return totalVehicles; }
    public void setTotalVehicles(int totalVehicles) { this.totalVehicles = totalVehicles; }
    public int getActiveReservations() { return activeReservations; }
    public void setActiveReservations(int activeReservations) { this.activeReservations = activeReservations; }
    public int getPendingInspections() { return pendingInspections; }
    public void setPendingInspections(int pendingInspections) { this.pendingInspections = pendingInspections; }
    public int getCompletedReservations() { return completedReservations; }
    public void setCompletedReservations(int completedReservations) { this.completedReservations = completedReservations; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
    public BigDecimal getOutstanding() { return outstanding; }
    public void setOutstanding(BigDecimal outstanding) { this.outstanding = outstanding; }
}
