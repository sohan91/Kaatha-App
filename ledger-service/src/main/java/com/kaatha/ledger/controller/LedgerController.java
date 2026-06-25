package com.kaatha.ledger.controller;

import com.kaatha.ledger.dto.request.LedgerUpdateRequest;
import com.kaatha.ledger.dto.response.ApiResponse;
import com.kaatha.ledger.dto.response.LedgerResponse;
import com.kaatha.ledger.service.LedgerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ledgers")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<LedgerResponse>> updateLedgerBalance(
            @Valid @RequestBody LedgerUpdateRequest request) {
        LedgerResponse response = ledgerService.updateLedgerBalance(request);
        return ResponseEntity.ok(
                ApiResponse.<LedgerResponse>builder()
                        .success(true)
                        .message("Ledger updated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/balance/{shopkeeperId}/{customerId}")
    public ResponseEntity<ApiResponse<LedgerResponse>> getLedger(
            @PathVariable Long shopkeeperId,
            @PathVariable Long customerId) {
        LedgerResponse response = ledgerService.getLedger(shopkeeperId, customerId);
        return ResponseEntity.ok(
                ApiResponse.<LedgerResponse>builder()
                        .success(true)
                        .message("Ledger fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<LedgerResponse>>> getLedgersByCustomer(
            @PathVariable Long customerId) {
        List<LedgerResponse> response = ledgerService.getLedgersByCustomer(customerId);
        return ResponseEntity.ok(
                ApiResponse.<List<LedgerResponse>>builder()
                        .success(true)
                        .message("Ledgers fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/shopkeeper/{shopkeeperId}")
    public ResponseEntity<ApiResponse<List<LedgerResponse>>> getLedgersByShopkeeper(
            @PathVariable Long shopkeeperId) {
        List<LedgerResponse> response = ledgerService.getLedgersByShopkeeper(shopkeeperId);
        return ResponseEntity.ok(
                ApiResponse.<List<LedgerResponse>>builder()
                        .success(true)
                        .message("Ledgers fetched successfully")
                        .data(response)
                        .build()
        );
    }
}
