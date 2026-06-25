package com.kaatha.report.feign;

import com.kaatha.report.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "TRANSACTION-SERVICE")
public interface TransactionClient {

    @GetMapping("/transactions/shopkeeper/{shopkeeperId}")
    ApiResponse<List<Map<String, Object>>> getByShopkeeper(@PathVariable Long shopkeeperId);

    @GetMapping("/transactions/customer/{customerId}")
    ApiResponse<List<Map<String, Object>>> getByCustomer(@PathVariable Long customerId);

    @GetMapping("/transactions/shopkeeper/{shopkeeperId}/today-collections")
    ApiResponse<Map<String, Object>> getTodayCollections(@PathVariable Long shopkeeperId);
}
