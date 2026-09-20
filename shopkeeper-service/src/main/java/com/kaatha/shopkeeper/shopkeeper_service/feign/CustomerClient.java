package com.kaatha.shopkeeper.shopkeeper_service.feign;

import com.kaatha.shopkeeper.shopkeeper_service.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name="CUSTOMER-SERVICE")
//public interface CustomerClient {
//
//    @GetMapping("/shopkeeper/{shopkeeperId}/search")
//    public ResponseEntity<ApiResponse<?>> searchCustomers(@PathVariable Long shopkeeperId);
//}
