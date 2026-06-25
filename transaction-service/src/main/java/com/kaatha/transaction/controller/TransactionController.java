package com.kaatha.transaction.controller;

import com.kaatha.transaction.dto.request.PaymentRequest;
import com.kaatha.transaction.dto.request.PurchaseRequest;
import com.kaatha.transaction.dto.response.ApiResponse;
import com.kaatha.transaction.dto.response.TransactionResponse;
import com.kaatha.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<TransactionResponse>> recordPurchase(
            @Valid @RequestBody PurchaseRequest request) {
        TransactionResponse response = transactionService.recordPurchase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<TransactionResponse>builder()
                        .success(true)
                        .message("Purchase recorded successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/payment")
    public ResponseEntity<ApiResponse<TransactionResponse>> recordPayment(
            @Valid @RequestBody PaymentRequest request) {
        TransactionResponse response = transactionService.recordPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<TransactionResponse>builder()
                        .success(true)
                        .message("Payment recorded successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByCustomer(
            @PathVariable Long customerId) {
        List<TransactionResponse> response = transactionService.getTransactionsByCustomer(customerId);
        return ResponseEntity.ok(
                ApiResponse.<List<TransactionResponse>>builder()
                        .success(true)
                        .message("Transactions retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/shopkeeper/{shopkeeperId}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByShopkeeper(
            @PathVariable Long shopkeeperId) {
        List<TransactionResponse> response = transactionService.getTransactionsByShopkeeper(shopkeeperId);
        return ResponseEntity.ok(
                ApiResponse.<List<TransactionResponse>>builder()
                        .success(true)
                        .message("Transactions retrieved successfully")
                        .data(response)
                        .build()
        );
    @GetMapping("/shopkeeper/{shopkeeperId}/today-collections")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTodayCollections(
            @PathVariable Long shopkeeperId) {

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Today's collections fetched")
                .data(transactionService.getTodayCollections(shopkeeperId))
                .build());
    }
}
