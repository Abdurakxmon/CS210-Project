package com.carrental.api.entity;

import com.carrental.api.entity.enums.VehicleStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "vehicles")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Vehicle extends BaseEntity {

    private String licenseNumber;
    private String stockNumber;
    private Integer passengerCapacity;
    private Boolean hasSunroof;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    private String model;
    private String make;
    private Integer manufacturingYear;
    private Integer mileage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private CarRentalLocation currentLocation;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "barcode_id")
    private Barcode barcodeDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_stall_id")
    private ParkingStall parkingStall;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleLog> vehicleLogs = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle")
    private List<VehicleReservation> reservations = new ArrayList<>();
}
