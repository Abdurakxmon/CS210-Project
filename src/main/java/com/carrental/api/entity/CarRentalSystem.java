package com.carrental.api.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
@Table(name = "car_rental_systems")
public class CarRentalSystem extends BaseEntity {

    private String name;

    @OneToMany(mappedBy = "rentalSystem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarRentalLocation> locations = new ArrayList<>();
}
