package com.carrental.api.service.impl;

import com.carrental.api.dto.request.AddVehicleLogRequest;
import com.carrental.api.dto.request.CreateVehicleRequest;
import com.carrental.api.entity.Vehicle;
import com.carrental.api.entity.VehicleLog;
import com.carrental.api.service.VehicleService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Override
    public Vehicle createVehicle(CreateVehicleRequest request) {
        throw new UnsupportedOperationException("Vehicle creation is not implemented yet");
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        throw new UnsupportedOperationException("Vehicle lookup is not implemented yet");
    }

    @Override
    public Vehicle getVehicleById(Long id) {
        throw new UnsupportedOperationException("Vehicle lookup is not implemented yet");
    }

    @Override
    public List<Vehicle> searchVehicles(String type, String model) {
        throw new UnsupportedOperationException("Vehicle search is not implemented yet");
    }

    @Override
    public VehicleLog addVehicleLog(AddVehicleLogRequest request) {
        throw new UnsupportedOperationException("Vehicle log creation is not implemented yet");
    }
}
