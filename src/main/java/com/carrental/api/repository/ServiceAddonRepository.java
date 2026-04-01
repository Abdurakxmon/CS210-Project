package com.carrental.api.repository;

import com.carrental.api.entity.ServiceAddon;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceAddonRepository extends JpaRepository<ServiceAddon, Long> {

    Optional<ServiceAddon> findByServiceId(String serviceId);
}
