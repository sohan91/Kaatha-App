package com.kaatha.shopkeeper.shopkeeper_service.service;

import com.kaatha.shopkeeper.shopkeeper_service.dto.request.RegisterShopkeeperRequest;
import com.kaatha.shopkeeper.shopkeeper_service.dto.request.ShopRegisterRequest;
import com.kaatha.shopkeeper.shopkeeper_service.dto.response.ShopRegisteredResponse;
import com.kaatha.shopkeeper.shopkeeper_service.exception.ShopNotFound;

public interface ShopService {
    ShopRegisteredResponse registerNewShop(ShopRegisterRequest request) throws ShopNotFound;
}
