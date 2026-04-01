package com.carrental.api.entity;

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
@Table(name = "parking_stalls")
public class ParkingStall extends BaseEntity {

    private String stallNumber;
    private String locationIdentifier;

    @OneToMany(mappedBy = "parkingStall")
    private List<Vehicle> vehicles = new ArrayList<>();
}
