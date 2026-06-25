package com.kaatha.report.feign;

import com.kaatha.report.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerClient {

    @GetMapping("/customers/shopkeeper/{shopkeeperId}")
    ApiResponse<List<Map<String, Object>>> getCustomers(@PathVariable Long shopkeeperId);

    @GetMapping("/customers/phone/{phoneNumber}/shops")
    ApiResponse<List<Map<String, Object>>> getShopsForPhone(@PathVariable String phoneNumber);
}
