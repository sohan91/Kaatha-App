package com.kaatha.gateway.api_gateway.util;

import org.springframework.http.HttpHeaders;

public class HeaderUtil {

    private HeaderUtil() {}

    public static String extractToken(HttpHeaders headers) {

        String authHeader =
                headers.getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            return null;
        }

        return authHeader.substring(7);
    }
}