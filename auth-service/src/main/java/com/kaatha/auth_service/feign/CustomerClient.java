package com.kaatha.auth_service.feign;

import com.kaatha.auth_service.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerClient {

    @GetMapping("/customers/exists/{phoneNumber}")
    ApiResponse<Boolean> existsByPhoneNumber(@PathVariable String phoneNumber);

    @GetMapping("/customers/default-shopkeeper/{phoneNumber}")
    ApiResponse<Long> getDefaultShopkeeper(@PathVariable String phoneNumber);
}