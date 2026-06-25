package com.kaatha.transaction.feign;

import com.kaatha.transaction.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "SHOPKEEPER-SERVICE")
public interface ShopkeeperClient {

    @GetMapping("/shopkeepers/{id}")
    ApiResponse<Map<String, Object>> getShopkeeper(@PathVariable Long id);
}
