package com.carrental.api.repository;

import com.carrental.api.entity.AdditionalDriver;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdditionalDriverRepository extends JpaRepository<AdditionalDriver, Long> {

    Optional<AdditionalDriver> findByDriverId(String driverId);
}
