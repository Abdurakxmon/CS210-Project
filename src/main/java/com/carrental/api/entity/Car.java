package com.carrental.api.entity;

import com.carrental.api.entity.enums.CarType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cars")
public class Car extends Vehicle {

    @Enumerated(EnumType.STRING)
    private CarType type;
}
