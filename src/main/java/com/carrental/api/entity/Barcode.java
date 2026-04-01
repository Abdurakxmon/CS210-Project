package com.carrental.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "barcodes")
public class Barcode extends BaseEntity {

    private String barcode;
    private LocalDate issuedAt;
    private Boolean active;

    @OneToOne(mappedBy = "barcodeDetails")
    private Vehicle vehicle;
}
