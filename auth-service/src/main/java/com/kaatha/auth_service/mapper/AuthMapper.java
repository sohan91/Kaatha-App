package com.kaatha.auth_service.mapper;

import com.kaatha.auth_service.entity.UserSession;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public UserSession toSession(String phoneNumber, String token) {

        return UserSession.builder()
                .phoneNumber(phoneNumber)
                .jwtToken(token)
                .active(true)
                .build();
    }
}