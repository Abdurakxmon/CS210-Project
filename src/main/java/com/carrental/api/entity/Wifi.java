package com.carrental.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "wifi_addons")
public class Wifi extends ServiceAddon {
}
