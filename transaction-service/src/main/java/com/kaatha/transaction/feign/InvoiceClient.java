package com.kaatha.transaction.feign;

import com.kaatha.transaction.dto.request.GenerateInvoiceRequest;
import com.kaatha.transaction.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "INVOICE-SERVICE")
public interface InvoiceClient {

    @PostMapping("/invoices/generate")
    ApiResponse<Map<String, Object>> generateInvoice(@RequestBody GenerateInvoiceRequest request);
}
