package com.carrental.api.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Person {

    private String name;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "streetAddress", column = @Column(name = "person_street_address")),
        @AttributeOverride(name = "city", column = @Column(name = "person_city")),
        @AttributeOverride(name = "state", column = @Column(name = "person_state")),
        @AttributeOverride(name = "zipcode", column = @Column(name = "person_zipcode")),
        @AttributeOverride(name = "country", column = @Column(name = "person_country"))
    })
    private Location address;

    private String email;
    private String phone;
}
