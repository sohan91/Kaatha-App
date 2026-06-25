package com.kaatha.auth_service.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {

        // For now: phone-based auth system
        return User.builder()
                .username(username)
                .password("") // OTP-based system
                .roles("USER")
                .build();
    }
}