package com.kaatha.shopkeeper.shopkeeper_service.controller;

import com.kaatha.shopkeeper.shopkeeper_service.dto.request.RegisterShopkeeperRequest;
import com.kaatha.shopkeeper.shopkeeper_service.dto.request.UpdateShopkeeperRequest;
import com.kaatha.shopkeeper.shopkeeper_service.dto.response.ApiResponse;
import com.kaatha.shopkeeper.shopkeeper_service.dto.response.ShopkeeperResponse;
import com.kaatha.shopkeeper.shopkeeper_service.service.ShopkeeperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shopkeepers")
@RequiredArgsConstructor
public class ShopkeeperController {

    private final ShopkeeperService shopkeeperService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ShopkeeperResponse>>
    registerShopkeeper(
            @Valid @RequestBody RegisterShopkeeperRequest request) {

        ShopkeeperResponse response =
                shopkeeperService.registerShopkeeper(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<ShopkeeperResponse>builder()
                                .success(true)
                                .message("Shopkeeper registered successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShopkeeperResponse>>
    getShopkeeper(@PathVariable Long id) {

        ShopkeeperResponse response =
                shopkeeperService.getShopkeeper(id);

        return ResponseEntity.ok(
                ApiResponse.<ShopkeeperResponse>builder()
                        .success(true)
                        .message("Shopkeeper fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShopkeeperResponse>>
    updateShopkeeper(
            @PathVariable Long id,
            @RequestBody UpdateShopkeeperRequest request) {

        ShopkeeperResponse response =
                shopkeeperService.updateShopkeeper(id, request);

        return ResponseEntity.ok(
                ApiResponse.<ShopkeeperResponse>builder()
                        .success(true)
                        .message("Shopkeeper updated successfully")
                        .data(response)
                        .build()
        );
    }
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<Boolean> existsByPhoneNumber(
            @PathVariable String phoneNumber) {

        boolean exists =
                shopkeeperService.existsByPhoneNumber(phoneNumber);

        return ResponseEntity.ok(exists);
    }

    @GetMapping("/phone/{phoneNumber}/details")
    public ResponseEntity<ApiResponse<ShopkeeperResponse>> getShopkeeperByPhone(
            @PathVariable String phoneNumber) {

        ShopkeeperResponse response =
                shopkeeperService.getShopkeeperByPhone(phoneNumber);

        return ResponseEntity.ok(
                ApiResponse.<ShopkeeperResponse>builder()
                        .success(true)
                        .message("Shopkeeper details fetched successfully")
                        .data(response)
                        .build()
        );
    }

}