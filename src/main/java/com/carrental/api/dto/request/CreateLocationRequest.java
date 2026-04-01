package com.carrental.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record CreateLocationRequest(
    @NotBlank String name,
    @Valid LocationRequest address
) {
}
