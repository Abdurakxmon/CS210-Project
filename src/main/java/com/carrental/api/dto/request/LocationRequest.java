package com.carrental.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LocationRequest(
    @NotBlank String streetAddress,
    @NotBlank String city,
    @NotBlank String state,
    @NotBlank String zipcode,
    @NotBlank String country
) {
}
