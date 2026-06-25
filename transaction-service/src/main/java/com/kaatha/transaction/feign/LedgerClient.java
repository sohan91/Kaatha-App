package com.kaatha.transaction.feign;

import com.kaatha.transaction.dto.request.LedgerUpdateRequest;
import com.kaatha.transaction.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "LEDGER-SERVICE")
public interface LedgerClient {

    @PostMapping("/ledgers/update")
    ApiResponse<?> updateLedgerBalance(@RequestBody LedgerUpdateRequest request);
}
