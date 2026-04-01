package com.carrental.api.service;

import com.carrental.api.dto.request.AddVehicleLogRequest;
import com.carrental.api.dto.request.CreateVehicleRequest;
import com.carrental.api.entity.Vehicle;
import com.carrental.api.entity.VehicleLog;
import java.util.List;

public interface VehicleService {

    Vehicle createVehicle(CreateVehicleRequest request);

    List<Vehicle> getAllVehicles();

    Vehicle getVehicleById(Long id);

    List<Vehicle> searchVehicles(String type, String model);

    VehicleLog addVehicleLog(AddVehicleLogRequest request);
}
