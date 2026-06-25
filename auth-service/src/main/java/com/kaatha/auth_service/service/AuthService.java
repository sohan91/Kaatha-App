package com.kaatha.auth_service.service;

import com.kaatha.auth_service.dto.request.RefreshTokenRequest;
import com.kaatha.auth_service.dto.request.SendOtpRequest;
import com.kaatha.auth_service.dto.request.VerifyOtpRequest;
import com.kaatha.auth_service.dto.response.LoginResponse;

public interface AuthService {

    String sendOtp(SendOtpRequest request);

    LoginResponse verifyOtp(VerifyOtpRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

    void logout(String phoneNumber);
}
