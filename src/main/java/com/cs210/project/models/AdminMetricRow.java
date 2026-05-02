package com.cs210.project.models;

public class AdminMetricRow {
    private final String name;
    private final String detail;
    private final int count;
    private final String amount;

    public AdminMetricRow(String name, String detail, int count, String amount) {
        this.name = name;
        this.detail = detail;
        this.count = count;
        this.amount = amount;
    }

    public String getName() { return name; }
    public String getDetail() { return detail; }
    public int getCount() { return count; }
    public String getAmount() { return amount; }
}
