package com.kaatha.shopkeeper.shopkeeper_service.mapper;

import com.kaatha.shopkeeper.shopkeeper_service.dto.request.ShopRegisterRequest;
import com.kaatha.shopkeeper.shopkeeper_service.dto.response.ShopRegisteredResponse;
import com.kaatha.shopkeeper.shopkeeper_service.entity.Shop;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ShopMapper {

    public Shop toEntity(ShopRegisterRequest request)
    {
        return
                 Shop.builder()
                        .shopkeeperId(request.getShopkeeperId())
                        .shopName(request.getShopName())
                         .city(request.getCity())
                         .state(request.getState())
                         .pinCode(request.getPinCode())
                        .businessType(request.getBusinessType())
                        .addressLineOne(request.getAddressLineOne())
                        .addressLineSecond(request.getAddressLineSecond())
                         .build();
    }

    public ShopRegisteredResponse toResponse(Shop shop)
    {
        return ShopRegisteredResponse.builder()
                .id(shop.getId())
                .shopkeeperId(shop.getShopkeeperId())
                .shopName(shop.getShopName())
                .businessType(shop.getBusinessType())
                .addressLineOne(shop.getAddressLineOne())
                .addressLineOne(shop.getAddressLineSecond())
                .city(shop.getCity())
                .state(shop.getState())
                .pinCode(shop.getPinCode())
                .createdAt(LocalDate.from(shop.getCreatedAt()))
                .updatedAt(LocalDate.from(shop.getUpdatedAt())).build();
    }
}
