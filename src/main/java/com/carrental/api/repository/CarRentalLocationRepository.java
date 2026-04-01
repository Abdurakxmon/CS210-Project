package com.carrental.api.repository;

import com.carrental.api.entity.CarRentalLocation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRentalLocationRepository extends JpaRepository<CarRentalLocation, Long> {

    List<CarRentalLocation> findByNameContainingIgnoreCase(String name);
}
