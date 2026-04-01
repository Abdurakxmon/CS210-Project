package com.carrental.api.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "car_rental_locations")
public class CarRentalLocation extends BaseEntity {

    private String name;

    @Embedded
    private Location address;

    @ManyToOne(fetch = FetchType.LAZY)
    private CarRentalSystem rentalSystem;

    @OneToMany(mappedBy = "currentLocation", cascade = CascadeType.ALL)
    private List<Vehicle> vehicles = new ArrayList<>();
}
