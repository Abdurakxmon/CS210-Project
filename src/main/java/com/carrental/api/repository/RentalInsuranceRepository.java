package com.carrental.api.repository;

import com.carrental.api.entity.RentalInsurance;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalInsuranceRepository extends JpaRepository<RentalInsurance, Long> {

    Optional<RentalInsurance> findByInsuranceId(String insuranceId);
}
