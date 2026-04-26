package com.cs210.project.models;

public class RentalSystem {
    private int id;
    private String name;

    public RentalSystem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}
