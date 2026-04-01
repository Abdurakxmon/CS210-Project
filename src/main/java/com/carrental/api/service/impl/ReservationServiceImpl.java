package com.carrental.api.service.impl;

import com.carrental.api.dto.request.CreateReservationRequest;
import com.carrental.api.entity.VehicleReservation;
import com.carrental.api.service.ReservationService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Override
    public VehicleReservation createReservation(CreateReservationRequest request) {
        throw new UnsupportedOperationException("Reservation creation is not implemented yet");
    }

    @Override
    public List<VehicleReservation> getAllReservations() {
        throw new UnsupportedOperationException("Reservation lookup is not implemented yet");
    }

    @Override
    public VehicleReservation getReservationById(Long id) {
        throw new UnsupportedOperationException("Reservation lookup is not implemented yet");
    }
}
