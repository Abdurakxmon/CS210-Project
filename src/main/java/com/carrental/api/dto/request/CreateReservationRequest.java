package com.carrental.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateReservationRequest(
    @NotNull Long memberId,
    @NotNull Long vehicleId,
    @NotBlank String pickupLocationName,
    @NotBlank String returnLocationName,
    @NotNull LocalDateTime dueDate
) {
}
