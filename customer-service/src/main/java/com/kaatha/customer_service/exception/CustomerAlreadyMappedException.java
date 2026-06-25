package com.kaatha.customer_service.exception;

import org.springframework.stereotype.Component;

public class CustomerAlreadyMappedException extends RuntimeException {

    public CustomerAlreadyMappedException(String message) {
        super(message);
    }
}