package com.carrental.api.repository;

import com.carrental.api.entity.Bill;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByReservationId(Long reservationId);
}
