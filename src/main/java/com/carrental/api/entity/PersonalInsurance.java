package com.carrental.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "personal_insurances")
public class PersonalInsurance extends RentalInsurance {
}
