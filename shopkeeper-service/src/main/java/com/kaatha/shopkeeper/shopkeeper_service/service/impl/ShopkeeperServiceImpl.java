package com.kaatha.shopkeeper.shopkeeper_service.service.impl;


import com.kaatha.shopkeeper.shopkeeper_service.constants.AppConstants;
import com.kaatha.shopkeeper.shopkeeper_service.dto.request.RegisterShopkeeperRequest;
import com.kaatha.shopkeeper.shopkeeper_service.dto.request.UpdateShopkeeperRequest;
import com.kaatha.shopkeeper.shopkeeper_service.dto.response.ShopkeeperResponse;
import com.kaatha.shopkeeper.shopkeeper_service.entity.Shopkeeper;
import com.kaatha.shopkeeper.shopkeeper_service.exception.ShopkeeperAlreadyExistsException;
import com.kaatha.shopkeeper.shopkeeper_service.exception.ShopkeeperNotFoundException;
import com.kaatha.shopkeeper.shopkeeper_service.mapper.ShopkeeperMapper;
import com.kaatha.shopkeeper.shopkeeper_service.repository.ShopkeeperRepository;
import com.kaatha.shopkeeper.shopkeeper_service.service.ShopkeeperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.OptionalLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopkeeperServiceImpl
        implements ShopkeeperService {

    private final ShopkeeperRepository shopkeeperRepository;
    private final ShopkeeperMapper shopkeeperMapper;

    @Override
    public ShopkeeperResponse registerShopkeeper(
            RegisterShopkeeperRequest request) {

        if (shopkeeperRepository.existsByPhoneNumber(
                request.getPhoneNumber())) {

            throw new ShopkeeperAlreadyExistsException(
                   AppConstants.SHOPKEEPER_ALREADY_EXIST
                            + request.getPhoneNumber());
        }

        // Validate Banking Information Securely
        if (request.getIfscCode() == null || !request.getIfscCode().matches("^[A-Z]{4}0[A-Z0-9]{6}$")) {
            throw new IllegalArgumentException("Invalid IFSC code format. Must match standard Indian Bank IFSC pattern.");
        }
        if (request.getAccountNumber() == null || !request.getAccountNumber().matches("^\\d{9,18}$")) {
            throw new IllegalArgumentException("Invalid bank account number. Must be numeric and between 9 and 18 digits.");
        }

        Shopkeeper shopkeeper =
                shopkeeperMapper.toEntity(request);

        // Create and link a mock Razorpay account for the shopkeeper
        String mockRazorpayId = "acc_" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 14);
        shopkeeper.setRazorpayAccountId(mockRazorpayId);

        Shopkeeper savedShopkeeper =
                shopkeeperRepository.save(shopkeeper);

        return shopkeeperMapper.toResponse(savedShopkeeper);
    }

    @Override
    public ShopkeeperResponse getShopkeeper(Long id) {

        Shopkeeper shopkeeper =
                shopkeeperRepository.findById(id)
                        .orElseThrow(() ->
                                new ShopkeeperNotFoundException(
                                        "Shopkeeper not found with id: "
                                                + id));

        return shopkeeperMapper.toResponse(shopkeeper);
    }

    @Override
    public ShopkeeperResponse updateShopkeeper(
            Long id,
            UpdateShopkeeperRequest request) {

        Shopkeeper shopkeeper =
                shopkeeperRepository.findById(id)
                        .orElseThrow(() ->
                                new ShopkeeperNotFoundException(
                                        "Shopkeeper not found with id: "
                                                + id));

        if (request.getShopName() != null) {
            shopkeeper.setShopName(request.getShopName());
        }

        if (request.getFirstName() != null) {
            shopkeeper.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            shopkeeper.setLastName(request.getLastName());
        }

        if (request.getEmail() != null) {
            shopkeeper.setEmail(request.getEmail());
        }

        if (request.getAddressLine1() != null) {
            shopkeeper.setAddressLine1(request.getAddressLine1());
        }

        if (request.getAddressLine2() != null) {
            shopkeeper.setAddressLine2(request.getAddressLine2());
        }

        if (request.getCity() != null) {
            shopkeeper.setCity(request.getCity());
        }

        if (request.getState() != null) {
            shopkeeper.setState(request.getState());
        }

        if (request.getPincode() != null) {
            shopkeeper.setPincode(request.getPincode());
        }

        Shopkeeper updatedShopkeeper =
                shopkeeperRepository.save(shopkeeper);

        return shopkeeperMapper.toResponse(updatedShopkeeper);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return shopkeeperRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean findShopkeeperById(Long Id) {
        return shopkeeperRepository.existsById(Id);
    }

    @Override
    public ShopkeeperResponse getShopkeeperByPhone(String phoneNumber) {
        Shopkeeper shopkeeper = shopkeeperRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ShopkeeperNotFoundException("Shopkeeper not found with phone: " + phoneNumber));
        return shopkeeperMapper.toResponse(shopkeeper);
    }

    @Override
    public Optional<Long> findIdByPhoneNumberShopKeeper(String phoneNumber)
    {
        Optional<Shopkeeper> shopkeeper = shopkeeperRepository.findByPhoneNumber(phoneNumber);
        return shopkeeper.get().getId().describeConstable();
    }

}