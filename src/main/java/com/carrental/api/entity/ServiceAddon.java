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
@Table(name = "service_addons")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ServiceAddon extends BaseEntity {

    private String serviceId;

    @ManyToMany(mappedBy = "serviceAddons")
    private List<VehicleReservation> reservations = new ArrayList<>();
}
