package com.carrental.api.repository;

import com.carrental.api.entity.VehicleReservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleReservationRepository extends JpaRepository<VehicleReservation, Long> {

    Optional<VehicleReservation> findByReservationNumber(String reservationNumber);

    List<VehicleReservation> findByMemberId(Long memberId);
}
