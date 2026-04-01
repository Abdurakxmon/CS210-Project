package com.carrental.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateMemberRequest(
    @Valid PersonRequest person,
    @NotBlank String password,
    @NotBlank String driverLicenseNumber,
    @NotNull LocalDate driverLicenseExpiry
) {
}
