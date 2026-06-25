package com.kaatha.auth_service.controller;

import com.kaatha.auth_service.dto.request.RefreshTokenRequest;
import com.kaatha.auth_service.dto.request.SendOtpRequest;
import com.kaatha.auth_service.dto.request.VerifyOtpRequest;
import com.kaatha.auth_service.dto.response.ApiResponse;
import com.kaatha.auth_service.dto.response.LoginResponse;
import com.kaatha.auth_service.security.JwtUtil;
import com.kaatha.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestBody SendOtpRequest request) {

        String response = authService.sendOtp(request);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("OTP sent successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyOtp(@RequestBody VerifyOtpRequest request) {

        LoginResponse response = authService.verifyOtp(request);

        return ResponseEntity.ok(
                ApiResponse.<LoginResponse>builder()
                        .success(true)
                        .message("Login successful")
                        .data(response)
                        .build()
        );
    }

//    @GetMapping("/validate")
//    public ResponseEntity<ApiResponse<Boolean>> validateToken(
//            @RequestParam("token") String token,
//            @RequestParam("phoneNumber") String phoneNumber) {
//
//        boolean isValid = jwtUtil.validateToken(token, phoneNumber);
//
//        if (isValid) {
//            return ResponseEntity.ok(
//                    ApiResponse.<Boolean>builder()
//                            .success(true)
//                            .message("Token is valid")
//                            .data(true)
//                            .build()
//            );
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                    ApiResponse.<Boolean>builder()
//                            .success(false)
//                            .message("Token is invalid or expired")
//                            .data(false)
//                            .build()
//            );
//        }
//    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @RequestBody RefreshTokenRequest request) {

        LoginResponse response = authService.refreshToken(request);

        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Token refreshed")
                .data(response)
                .build());
    }

    @PostMapping("/logout/{phoneNumber}")
    public ResponseEntity<ApiResponse<String>> logout(@PathVariable String phoneNumber) {

        authService.logout(phoneNumber);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Logout successful")
                        .data("Session cleared")
                        .build()
        );
    }
}