package com.kaatha.shopkeeper.shopkeeper_service.service;


import com.kaatha.shopkeeper.shopkeeper_service.dto.request.RegisterShopkeeperRequest;
import com.kaatha.shopkeeper.shopkeeper_service.dto.request.UpdateShopkeeperRequest;
import com.kaatha.shopkeeper.shopkeeper_service.dto.response.ShopkeeperResponse;

import java.util.List;

public interface ShopkeeperService {

    ShopkeeperResponse registerShopkeeper(
            RegisterShopkeeperRequest request);

    ShopkeeperResponse getShopkeeper(Long id);

    ShopkeeperResponse updateShopkeeper(
            Long id,
            UpdateShopkeeperRequest request);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean findShopkeeperById(Long Id);
    ShopkeeperResponse getShopkeeperByPhone(String phoneNumber);
}