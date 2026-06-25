package com.kaatha.shopkeeper.shopkeeper_service.exception;

public class ShopkeeperAlreadyExistsException
        extends RuntimeException {

    public ShopkeeperAlreadyExistsException(String message) {
        super(message);
    }
}