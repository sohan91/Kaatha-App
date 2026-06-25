package com.kaatha.shopkeeper.shopkeeper_service.exception;

public class ShopkeeperNotFoundException
        extends RuntimeException {

    public ShopkeeperNotFoundException(String message) {
        super(message);
    }
}