package com.kaatha.customer_service.repository;

import com.kaatha.customer_service.entity.CustomerPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerPreferenceRepository extends JpaRepository<CustomerPreference, Long> {

    Optional<CustomerPreference> findByPhoneNumber(String phoneNumber);
}
