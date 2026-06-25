package com.kaatha.shopkeeper.shopkeeper_service.service.impl;

import com.kaatha.shopkeeper.shopkeeper_service.dto.request.ShopRegisterRequest;
import com.kaatha.shopkeeper.shopkeeper_service.dto.response.ShopRegisteredResponse;
import com.kaatha.shopkeeper.shopkeeper_service.entity.Shop;
import com.kaatha.shopkeeper.shopkeeper_service.entity.Shopkeeper;
import com.kaatha.shopkeeper.shopkeeper_service.exception.ShopNotFound;
import com.kaatha.shopkeeper.shopkeeper_service.mapper.ShopMapper;
import com.kaatha.shopkeeper.shopkeeper_service.repository.ShopRepository;
import com.kaatha.shopkeeper.shopkeeper_service.repository.ShopkeeperRepository;
import com.kaatha.shopkeeper.shopkeeper_service.service.ShopService;
import com.kaatha.shopkeeper.shopkeeper_service.service.ShopkeeperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final ShopkeeperService shopkeeperService;
    private final ShopkeeperRepository shopkeeperRepository;
    private final ShopMapper shopMapper;


    @Override
    public ShopRegisteredResponse registerNewShop(ShopRegisterRequest request) throws ShopNotFound {
        Optional<Shopkeeper> isShopkeeperPresent = shopkeeperRepository.findById(request.getShopkeeperId());
        if(!isShopkeeperPresent.isPresent())
        {
                throw  new ShopNotFound("Shopkeeper does not found");
        }
        Shop shopEntity = shopMapper.toEntity(request);
        Shop shop =  shopRepository.save(shopEntity);
        return shopMapper.toResponse(shop);
    }
}
