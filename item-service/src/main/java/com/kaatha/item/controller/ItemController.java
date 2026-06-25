package com.kaatha.item.controller;

import com.kaatha.item.dto.request.ItemRequest;
import com.kaatha.item.dto.request.StockUpdateRequest;
import com.kaatha.item.dto.response.ApiResponse;
import com.kaatha.item.dto.response.ItemResponse;
import com.kaatha.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ApiResponse<ItemResponse>> createItem(
            @Valid @RequestBody ItemRequest request) {
        ItemResponse response = itemService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ItemResponse>builder()
                        .success(true)
                        .message("Item created successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/shopkeeper/{shopkeeperId}")
    public ResponseEntity<ApiResponse<List<ItemResponse>>> getItemsByShopkeeper(
            @PathVariable Long shopkeeperId) {
        List<ItemResponse> response = itemService.getItemsByShopkeeper(shopkeeperId);
        return ResponseEntity.ok(
                ApiResponse.<List<ItemResponse>>builder()
                        .success(true)
                        .message("Items fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponse>> getItemById(
            @PathVariable Long id) {
        ItemResponse response = itemService.getItemById(id);
        return ResponseEntity.ok(
                ApiResponse.<ItemResponse>builder()
                        .success(true)
                        .message("Item fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponse>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequest request) {
        ItemResponse response = itemService.updateItem(id, request);
        return ResponseEntity.ok(
                ApiResponse.<ItemResponse>builder()
                        .success(true)
                        .message("Item updated successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/stock")
    public ResponseEntity<ApiResponse<Void>> updateStock(
            @RequestBody List<StockUpdateRequest> requests) {
        itemService.updateStock(requests);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Stock updated successfully")
                        .build()
        );
    }

    @PostMapping("/list")
    public ResponseEntity<ApiResponse<List<ItemResponse>>> getItemsByIds(
            @RequestBody List<Long> ids) {
        List<ItemResponse> response = itemService.getItemsByIds(ids);
        return ResponseEntity.ok(
                ApiResponse.<List<ItemResponse>>builder()
                        .success(true)
                        .message("Items retrieved successfully")
                        .data(response)
                        .build()
        );
    }
}
