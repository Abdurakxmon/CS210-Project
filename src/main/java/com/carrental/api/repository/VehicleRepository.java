package com.carrental.api.repository;

import com.carrental.api.entity.Vehicle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByStockNumber(String stockNumber);

    List<Vehicle> findByModelContainingIgnoreCase(String model);
}
