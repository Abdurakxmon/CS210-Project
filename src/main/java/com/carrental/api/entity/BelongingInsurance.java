package com.carrental.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "belonging_insurances")
public class BelongingInsurance extends RentalInsurance {
}
