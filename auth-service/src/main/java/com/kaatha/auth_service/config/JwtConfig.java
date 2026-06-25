package com.kaatha.auth_service.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.security.Key;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    public Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}