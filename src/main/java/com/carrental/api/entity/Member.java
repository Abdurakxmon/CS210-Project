package com.carrental.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "members")
public class Member extends Account {

    private String driverLicenseNumber;
    private LocalDate driverLicenseExpiry;

    @OneToMany(mappedBy = "member")
    private List<VehicleReservation> reservations = new ArrayList<>();
}
