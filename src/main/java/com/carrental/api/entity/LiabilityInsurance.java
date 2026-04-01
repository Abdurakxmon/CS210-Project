package com.carrental.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "liability_insurances")
public class LiabilityInsurance extends RentalInsurance {
}
