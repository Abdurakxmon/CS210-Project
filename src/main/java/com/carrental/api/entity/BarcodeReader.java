package com.carrental.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "barcode_readers")
public class BarcodeReader extends BaseEntity {

    private String readerCode;
    private LocalDate registeredAt;
    private Boolean active;
}
