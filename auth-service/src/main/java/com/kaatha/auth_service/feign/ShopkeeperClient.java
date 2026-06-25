package com.kaatha.auth_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SHOPKEEPER-SERVICE")
public interface ShopkeeperClient {

    @GetMapping("/shopkeepers/phone/{phoneNumber}")
    Boolean existsByPhoneNumber(
            @PathVariable String phoneNumber
    );
}