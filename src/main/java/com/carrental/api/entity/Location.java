package com.carrental.api.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Location {

    private String streetAddress;
    private String city;
    private String state;
    private String zipcode;
    private String country;
}
