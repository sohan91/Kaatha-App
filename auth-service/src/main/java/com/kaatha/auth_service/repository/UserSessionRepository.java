package com.kaatha.auth_service.repository;

import com.kaatha.auth_service.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserSessionRepository
        extends JpaRepository<UserSession, Long> {

    List<UserSession> findByPhoneNumberAndActiveTrue(String phoneNumber);
}