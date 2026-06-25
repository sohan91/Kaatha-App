package com.kaatha.auth_service.service.impl;

import com.kaatha.auth_service.dto.request.SendOtpRequest;
import com.kaatha.auth_service.dto.request.VerifyOtpRequest;
import com.kaatha.auth_service.dto.response.ApiResponse;
import com.kaatha.auth_service.dto.response.LoginResponse;
import com.kaatha.auth_service.entity.RefreshToken;
import com.kaatha.auth_service.entity.UserSession;
import com.kaatha.auth_service.entity.enums.UserType;
import com.kaatha.auth_service.exception.InvalidOtpException;
import com.kaatha.auth_service.feign.CustomerClient;
import com.kaatha.auth_service.feign.ShopkeeperClient;
import com.kaatha.auth_service.repository.RefreshTokenRepository;
import com.kaatha.auth_service.repository.UserSessionRepository;
import com.kaatha.auth_service.security.JwtUtil;
import com.kaatha.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CustomerClient customerClient;
    private final ShopkeeperClient shopkeeperClient;

    private final JwtUtil jwtUtil;

    private final UserSessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // temporary OTP store (replace with Redis later)
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    // ---------------- SEND OTP ----------------
    @Override
    public String sendOtp(SendOtpRequest request) {

        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));

        otpStore.put(request.getPhoneNumber(), otp);

        System.out.println("OTP for " + request.getPhoneNumber() + " = " + otp);

        return "OTP sent successfully";
    }

    // ---------------- VERIFY OTP + LOGIN ----------------
    @Override
    public LoginResponse verifyOtp(VerifyOtpRequest request) {

        String storedOtp = otpStore.get(request.getPhoneNumber());

        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        otpStore.remove(request.getPhoneNumber());

        Long shopkeeperId = null;

        if (request.getUserType() == UserType.CUSTOMER) {

            ApiResponse<Boolean> exists = customerClient.existsByPhoneNumber(request.getPhoneNumber());

            if (exists == null) {
                throw new RuntimeException("Customer not found");
            }

            shopkeeperId =
                    customerClient.getDefaultShopkeeper(request.getPhoneNumber()).getData();

        } else if (request.getUserType() == UserType.SHOPKEEPER) {

            Boolean exists = shopkeeperClient.existsByPhoneNumber(request.getPhoneNumber());

            if (!exists) {
                throw new RuntimeException("Shopkeeper not found");
            }
        }

        String accessToken = jwtUtil.generateToken(request.getPhoneNumber());
        String refreshToken = jwtUtil.generateRefreshToken(request.getPhoneNumber());

        UserSession session = UserSession.builder()
                .phoneNumber(request.getPhoneNumber())
                .userType(request.getUserType())
                .jwtToken(accessToken)
                .active(true)
                .loginTime(LocalDateTime.now())
                .build();

        sessionRepository.save(session);

        RefreshToken token = RefreshToken.builder()
                .phoneNumber(request.getPhoneNumber())
                .refreshToken(refreshToken)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .active(true)
                .build();

        refreshTokenRepository.save(token);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userType(request.getUserType())
                .shopkeeperId(shopkeeperId)
                .build();
    }

    // ---------------- LOGOUT ----------------
    @Override
    public void logout(String phoneNumber) {

        // FIX: use LIST not OPTIONAL
        List<UserSession> sessions =
                sessionRepository.findByPhoneNumberAndActiveTrue(phoneNumber);

        if (sessions.isEmpty()) {
            throw new RuntimeException("No active session found for user");
        }

        sessions.forEach(session -> {
            session.setActive(false);
            session.setLogoutTime(LocalDateTime.now());
        });

        sessionRepository.saveAll(sessions);

        List<UserSession> tokens =
                sessionRepository.findByPhoneNumberAndActiveTrue(phoneNumber);

        tokens.forEach(token -> token.setActive(false));
    }
}