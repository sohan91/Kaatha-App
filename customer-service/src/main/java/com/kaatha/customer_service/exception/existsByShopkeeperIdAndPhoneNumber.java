package com.kaatha.customer_service.exception;

public class existsByShopkeeperIdAndPhoneNumber extends RuntimeException {
    public existsByShopkeeperIdAndPhoneNumber(String message) {
        super(message);
    }
}