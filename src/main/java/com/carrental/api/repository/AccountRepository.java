package com.carrental.api.repository;

import com.carrental.api.entity.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByPersonEmail(String email);
}
