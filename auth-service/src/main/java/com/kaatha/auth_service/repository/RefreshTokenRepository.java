package com.kaatha.auth_service.repository;

import com.kaatha.auth_service.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshTokenAndActiveTrue(String refreshToken);

    List<RefreshToken> findByPhoneNumberAndActiveTrue(String phoneNumber);
}
