package com.carrental.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
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
@Table(name = "equipments")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Equipment extends BaseEntity {

    private String equipmentId;

    @ManyToMany(mappedBy = "equipments")
    private List<VehicleReservation> reservations = new ArrayList<>();
}
