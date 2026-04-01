package com.carrental.api.service;

import com.carrental.api.dto.request.CreateLocationRequest;
import com.carrental.api.entity.CarRentalLocation;
import java.util.List;

public interface LocationService {

    CarRentalLocation createLocation(CreateLocationRequest request);

    List<CarRentalLocation> getAllLocations();

    CarRentalLocation getLocationById(Long id);
}
