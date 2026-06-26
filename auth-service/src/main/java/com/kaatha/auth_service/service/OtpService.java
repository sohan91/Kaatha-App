package com.kaatha.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private static final String OTP_PREFIX = "otp:";
    private static final String RATE_PREFIX = "otp:rate:";

    private final StringRedisTemplate redisTemplate;

    @Value("${otp.expiry-minutes:5}")
    private long expiryMinutes;

    @Value("${otp.max-attempts:5}")
    private int maxAttempts;

    public String generateAndStore(String phoneNumber) {
        String rateKey = RATE_PREFIX + phoneNumber;
        Long attempts = redisTemplate.opsForValue().increment(rateKey);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(rateKey, Duration.ofMinutes(15));
        }
        if (attempts != null && attempts > maxAttempts) {
            throw new IllegalStateException("Too many OTP requests. Please try again later.");
        }

        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));
        redisTemplate.opsForValue().set(
                OTP_PREFIX + phoneNumber,
                otp,
                expiryMinutes,
                TimeUnit.MINUTES);
        log.debug("OTP generated for {}", phoneNumber);
        return otp;
    }

    public boolean verify(String phoneNumber, String otp) {
        log.info("OTP from Redis: {}",redisTemplate.opsForValue().get(OTP_PREFIX+phoneNumber));
        String stored = redisTemplate.opsForValue().get(OTP_PREFIX + phoneNumber);
        if (stored == null || !stored.equals(otp)) {
            return false;
        }
        redisTemplate.delete(OTP_PREFIX + phoneNumber);
        return true;
    }
}
