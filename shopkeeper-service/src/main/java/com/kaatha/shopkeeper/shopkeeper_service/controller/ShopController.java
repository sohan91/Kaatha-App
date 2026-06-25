package com.kaatha.shopkeeper.shopkeeper_service.controller;

import com.kaatha.shopkeeper.shopkeeper_service.dto.request.ShopRegisterRequest;
import com.kaatha.shopkeeper.shopkeeper_service.dto.response.ShopRegisteredResponse;
import com.kaatha.shopkeeper.shopkeeper_service.exception.ShopNotFound;
import com.kaatha.shopkeeper.shopkeeper_service.repository.ShopRepository;
import com.kaatha.shopkeeper.shopkeeper_service.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

   private final ShopService shopService;

   @PostMapping("/register")
    public ResponseEntity<ShopRegisteredResponse> registerShop(@Valid@RequestBody ShopRegisterRequest request) throws ShopNotFound {
       ShopRegisteredResponse response =shopService.registerNewShop(request);
       return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }
}
