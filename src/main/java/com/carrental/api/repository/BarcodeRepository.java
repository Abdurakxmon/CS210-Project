package com.carrental.api.repository;

import com.carrental.api.entity.Barcode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarcodeRepository extends JpaRepository<Barcode, Long> {

    Optional<Barcode> findByBarcode(String barcode);
}
