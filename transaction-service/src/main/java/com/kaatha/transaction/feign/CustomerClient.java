package com.kaatha.transaction.feign;

import com.kaatha.transaction.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerClient {

    @GetMapping("/customers/{customerId}")
    ApiResponse<Map<String, Object>> getCustomer(@PathVariable Long customerId);
}
