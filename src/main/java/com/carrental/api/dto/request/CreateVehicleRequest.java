package com.carrental.api.dto.request;

import com.carrental.api.entity.enums.VehicleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateVehicleRequest(
    @NotBlank String vehicleType,
    String subtype,
    @NotBlank String licenseNumber,
    @NotBlank String stockNumber,
    @NotBlank String model,
    @NotBlank String make,
    @NotNull Integer manufacturingYear,
    @NotNull Integer mileage,
    @NotNull Integer passengerCapacity,
    @NotNull Boolean hasSunroof,
    @NotNull VehicleStatus status,
    Long locationId,
    Long parkingStallId,
    String barcode
) {
}
