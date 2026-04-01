package com.carrental.api.dto.request;

import com.carrental.api.entity.enums.VehicleLogType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddVehicleLogRequest(
    @NotNull Long vehicleId,
    @NotNull VehicleLogType type,
    @NotBlank String description
) {
}
