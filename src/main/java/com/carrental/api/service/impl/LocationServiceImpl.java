package com.carrental.api.service.impl;

import com.carrental.api.dto.request.CreateLocationRequest;
import com.carrental.api.entity.CarRentalLocation;
import com.carrental.api.service.LocationService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LocationServiceImpl implements LocationService {

    @Override
    public CarRentalLocation createLocation(CreateLocationRequest request) {
        throw new UnsupportedOperationException("Location creation is not implemented yet");
    }

    @Override
    public List<CarRentalLocation> getAllLocations() {
        throw new UnsupportedOperationException("Location lookup is not implemented yet");
    }

    @Override
    public CarRentalLocation getLocationById(Long id) {
        throw new UnsupportedOperationException("Location lookup is not implemented yet");
    }
}
