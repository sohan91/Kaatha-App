package com.kaatha.auth_service.repository;

import com.kaatha.auth_service.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpVerificationRepository
        extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification>
    findTopByPhoneNumberOrderByIdDesc(String phoneNumber);
}