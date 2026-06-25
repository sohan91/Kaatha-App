package com.kaatha.transaction.feign;

import com.kaatha.transaction.dto.request.StockUpdateRequest;
import com.kaatha.transaction.dto.response.ApiResponse;
import com.kaatha.transaction.dto.response.ItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "ITEM-SERVICE")
public interface ItemClient {

    @GetMapping("/items/{id}")
    ApiResponse<ItemResponse> getItemById(@PathVariable("id") Long id);

    @PutMapping("/items/stock")
    ApiResponse<Void> updateStock(@RequestBody List<StockUpdateRequest> requests);

    @PostMapping("/items/list")
    ApiResponse<List<ItemResponse>> getItemsByIds(@RequestBody List<Long> ids);
}
