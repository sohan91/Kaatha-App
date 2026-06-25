package com.kaatha.payment.feign;

import com.kaatha.payment.dto.response.ApiResponse;
import com.kaatha.payment.dto.response.ShopkeeperResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SHOPKEEPER-SERVICE")
public interface ShopkeeperClient {

    @GetMapping("/shopkeepers/{id}")
    ApiResponse<ShopkeeperResponse> getShopkeeper(@PathVariable Long id);
}
