package com.carrental.api.repository;

import com.carrental.api.entity.CarRentalSystem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRentalSystemRepository extends JpaRepository<CarRentalSystem, Long> {

    Optional<CarRentalSystem> findByNameIgnoreCase(String name);
}
