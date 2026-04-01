package com.carrental.api.repository;

import com.carrental.api.entity.ParkingStall;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingStallRepository extends JpaRepository<ParkingStall, Long> {

    Optional<ParkingStall> findByStallNumber(String stallNumber);
}
