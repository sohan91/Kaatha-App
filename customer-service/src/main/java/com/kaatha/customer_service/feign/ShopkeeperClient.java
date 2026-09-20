package com.kaatha.customer_service.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "SHOPKEEPER-SERVICE")
public interface ShopkeeperClient {

    @GetMapping("/api/phone/{phoneNumber}")
    public ResponseEntity<Optional<Long>> findIdShopKeeper(@Valid @PathVariable String phoneNumber);
}
