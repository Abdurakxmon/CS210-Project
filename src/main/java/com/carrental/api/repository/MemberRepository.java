package com.carrental.api.repository;

import com.carrental.api.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByDriverLicenseNumber(String driverLicenseNumber);
}
