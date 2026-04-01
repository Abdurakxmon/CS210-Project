package com.carrental.api.repository;

import com.carrental.api.entity.VehicleLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleLogRepository extends JpaRepository<VehicleLog, Long> {

    Optional<VehicleLog> findByLogIdentifier(String logIdentifier);

    List<VehicleLog> findByVehicleId(Long vehicleId);
}
