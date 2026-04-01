package com.carrental.api.service;

import com.carrental.api.dto.request.CreateReservationRequest;
import com.carrental.api.entity.VehicleReservation;
import java.util.List;

public interface ReservationService {

    VehicleReservation createReservation(CreateReservationRequest request);

    List<VehicleReservation> getAllReservations();

    VehicleReservation getReservationById(Long id);
}
