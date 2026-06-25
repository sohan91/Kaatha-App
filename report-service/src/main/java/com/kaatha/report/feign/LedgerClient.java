package com.kaatha.report.feign;

import com.kaatha.report.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "LEDGER-SERVICE")
public interface LedgerClient {

    @GetMapping("/ledgers/shopkeeper/{shopkeeperId}")
    ApiResponse<List<Map<String, Object>>> getLedgersByShopkeeper(@PathVariable Long shopkeeperId);

    @GetMapping("/ledgers/customer/{customerId}")
    ApiResponse<List<Map<String, Object>>> getLedgersByCustomer(@PathVariable Long customerId);
}
