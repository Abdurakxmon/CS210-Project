package com.carrental.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PersonRequest(
    @NotBlank String name,
    @Valid LocationRequest address,
    @Email @NotBlank String email,
    @NotBlank String phone
) {
}
