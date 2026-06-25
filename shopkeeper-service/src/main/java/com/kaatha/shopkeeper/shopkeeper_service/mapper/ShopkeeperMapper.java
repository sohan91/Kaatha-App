package com.kaatha.shopkeeper.shopkeeper_service.mapper;

import com.kaatha.shopkeeper.shopkeeper_service.dto.request.RegisterShopkeeperRequest;
import com.kaatha.shopkeeper.shopkeeper_service.dto.response.ShopkeeperResponse;
import com.kaatha.shopkeeper.shopkeeper_service.entity.Shopkeeper;
import org.springframework.stereotype.Component;

@Component
public class ShopkeeperMapper {

    public Shopkeeper toEntity(
            RegisterShopkeeperRequest request) {

        return Shopkeeper.builder()
                .shopName(request.getShopName())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .gstNumber(request.getGstNumber())
                .shopCategory(request.getShopCategory())
                .accountHolderName(request.getAccountHolderName())
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .ifscCode(request.getIfscCode())
                .active(true)
                .build();
    }

    public ShopkeeperResponse toResponse(
            Shopkeeper shopkeeper) {

        return ShopkeeperResponse.builder()
                .id(shopkeeper.getId())
                .shopName(shopkeeper.getShopName())
                .firstName(shopkeeper.getFirstName())
                .lastName(shopkeeper.getLastName())
                .phoneNumber(shopkeeper.getPhoneNumber())
                .email(shopkeeper.getEmail())
                .addressLine1(shopkeeper.getAddressLine1())
                .addressLine2(shopkeeper.getAddressLine2())
                .city(shopkeeper.getCity())
                .state(shopkeeper.getState())
                .pincode(shopkeeper.getPincode())
                .gstNumber(shopkeeper.getGstNumber())
                .shopCategory(shopkeeper.getShopCategory())
                .accountHolderName(shopkeeper.getAccountHolderName())
                .bankName(shopkeeper.getBankName())
                .accountNumber(shopkeeper.getAccountNumber())
                .ifscCode(shopkeeper.getIfscCode())
                .razorpayAccountId(shopkeeper.getRazorpayAccountId())
                .active(shopkeeper.getActive())
                .build();
    }
}