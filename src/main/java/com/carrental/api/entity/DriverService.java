package com.carrental.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "driver_services")
public class DriverService extends ServiceAddon {
}
