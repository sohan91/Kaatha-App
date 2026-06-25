package com.kaatha.auth_service.service.impl;

import com.kaatha.auth_service.dto.request.NotificationRequest;
import com.kaatha.auth_service.dto.request.RefreshTokenRequest;
import com.kaatha.auth_service.dto.request.SendOtpRequest;
import com.kaatha.auth_service.dto.request.VerifyOtpRequest;
import com.kaatha.auth_service.dto.response.ApiResponse;
import com.kaatha.auth_service.dto.response.LoginResponse;
import com.kaatha.auth_service.entity.RefreshToken;
import com.kaatha.auth_service.entity.UserSession;
import com.kaatha.auth_service.entity.enums.UserType;
import com.kaatha.auth_service.exception.InvalidOtpException;
import com.kaatha.auth_service.feign.CustomerClient;
import com.kaatha.auth_service.feign.NotificationClient;
import com.kaatha.auth_service.feign.ShopkeeperClient;
import com.kaatha.auth_service.repository.RefreshTokenRepository;
import com.kaatha.auth_service.repository.UserSessionRepository;
import com.kaatha.auth_service.security.JwtUtil;
import com.kaatha.auth_service.service.AuthService;
import com.kaatha.auth_service.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CustomerClient customerClient;
    private final ShopkeeperClient shopkeeperClient;
    private final NotificationClient notificationClient;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final UserSessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public String sendOtp(SendOtpRequest request) {
        if (request.getUserType() == UserType.SHOPKEEPER
                && Boolean.TRUE.equals(request.getForRegistration())
                && shopkeeperClient.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalStateException("Phone number already registered. Please login.");
        }

        if (request.getUserType() == UserType.CUSTOMER
                && !Boolean.TRUE.equals(request.getForRegistration())) {
            ApiResponse<Boolean> exists = customerClient.existsByPhoneNumber(request.getPhoneNumber());
            if (exists == null || !Boolean.TRUE.equals(exists.getData())) {
                throw new IllegalStateException(
                        "You are currently not associated with any shopkeeper.");
            }
        }

        String otp = otpService.generateAndStore(request.getPhoneNumber());

        try {
            notificationClient.sendNotification(NotificationRequest.builder()
                    .recipientPhone(request.getPhoneNumber())
                    .message("Your Kaatha OTP is: " + otp + ". Valid for 5 minutes.")
                    .type("OTP")
                    .build());
        } catch (Exception e) {
            log.warn("Failed to send OTP via notification service, OTP logged for dev", e);
            log.info("DEV OTP for {} = {}", request.getPhoneNumber(), otp);
        }

        return "OTP sent successfully";
    }

    @Override
    public LoginResponse verifyOtp(VerifyOtpRequest request) {
        if (!otpService.verify(request.getPhoneNumber(), request.getOtp())) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }

        Long shopkeeperId = null;

        if (request.getUserType() == UserType.CUSTOMER) {
            ApiResponse<Boolean> exists = customerClient.existsByPhoneNumber(request.getPhoneNumber());
            if (exists == null || !Boolean.TRUE.equals(exists.getData())) {
                throw new IllegalStateException(
                        "You are currently not associated with any shopkeeper.");
            }
            shopkeeperId = customerClient.getDefaultShopkeeper(request.getPhoneNumber()).getData();
        } else if (request.getUserType() == UserType.SHOPKEEPER) {
            if (!shopkeeperClient.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new IllegalStateException("Shopkeeper not found. Please register first.");
            }
        }

        return createSession(request.getPhoneNumber(), request.getUserType(), shopkeeperId);
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenRepository
                .findByRefreshTokenAndActiveTrue(request.getRefreshToken())
                .orElseThrow(() -> new InvalidOtpException("Invalid refresh token"));

        if (stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            stored.setActive(false);
            refreshTokenRepository.save(stored);
            throw new InvalidOtpException("Refresh token expired");
        }

        UserSession session = sessionRepository
                .findFirstByPhoneNumberAndActiveTrueOrderByLoginTimeDesc(stored.getPhoneNumber())
                .orElseThrow(() -> new InvalidOtpException("No active session found"));

        Long shopkeeperId = null;
        if (session.getUserType() == UserType.CUSTOMER) {
            shopkeeperId = customerClient.getDefaultShopkeeper(stored.getPhoneNumber()).getData();
        }

        return createSession(stored.getPhoneNumber(), session.getUserType(), shopkeeperId);
    }

    @Override
    public void logout(String phoneNumber) {
        List<UserSession> sessions = sessionRepository.findByPhoneNumberAndActiveTrue(phoneNumber);
        sessions.forEach(session -> {
            session.setActive(false);
            session.setLogoutTime(LocalDateTime.now());
        });
        sessionRepository.saveAll(sessions);

        List<RefreshToken> tokens = refreshTokenRepository.findByPhoneNumberAndActiveTrue(phoneNumber);
        tokens.forEach(token -> token.setActive(false));
        refreshTokenRepository.saveAll(tokens);
    }

    private LoginResponse createSession(String phoneNumber, UserType userType, Long shopkeeperId) {
        String accessToken = jwtUtil.generateToken(phoneNumber, userType.name());
        String refreshToken = jwtUtil.generateRefreshToken(phoneNumber);

        sessionRepository.save(UserSession.builder()
                .phoneNumber(phoneNumber)
                .userType(userType)
                .jwtToken(accessToken)
                .active(true)
                .loginTime(LocalDateTime.now())
                .build());

        refreshTokenRepository.save(RefreshToken.builder()
                .phoneNumber(phoneNumber)
                .refreshToken(refreshToken)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .active(true)
                .build());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userType(userType)
                .shopkeeperId(shopkeeperId)
                .build();
    }
}
