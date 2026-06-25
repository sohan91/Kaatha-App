package com.kaatha.customer_service.controller;

import com.kaatha.customer_service.dto.request.RegisterCustomerRequest;
import com.kaatha.customer_service.dto.request.SetDefaultShopRequest;
import com.kaatha.customer_service.dto.response.ApiResponse;
import com.kaatha.customer_service.dto.response.CustomerResponse;
import com.kaatha.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerResponse>> registerCustomer(
            @Valid @RequestBody RegisterCustomerRequest request) {

        CustomerResponse response = customerService.registerCustomer(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CustomerResponse>builder()
                        .success(true)
                        .message("Customer registered successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(ApiResponse.<CustomerResponse>builder()
                .success(true)
                .message("Customer fetched successfully")
                .data(customerService.getCustomerById(customerId))
                .build());
    }

    @GetMapping("/shopkeeper/{shopkeeperId}/phone/{phoneNumber}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getByShopkeeperAndPhone(
            @PathVariable Long shopkeeperId,
            @PathVariable String phoneNumber) {

        return ResponseEntity.ok(ApiResponse.<CustomerResponse>builder()
                .success(true)
                .message("Customer fetched successfully")
                .data(customerService.getCustomerByShopkeeperAndPhone(shopkeeperId, phoneNumber))
                .build());
    }

    @GetMapping("/shopkeeper/{shopkeeperId}")
    public ResponseEntity<ApiResponse<?>> getByShopkeeper(@PathVariable Long shopkeeperId) {

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Customers fetched successfully")
                .data(customerService.getCustomersByShopkeeper(shopkeeperId))
                .build());
    }

    @GetMapping("/phone/{phoneNumber}/shops")
    public ResponseEntity<ApiResponse<?>> getShopkeepersForPhone(@PathVariable String phoneNumber) {

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Shopkeeper associations fetched successfully")
                .data(customerService.getShopkeepersForPhone(phoneNumber))
                .build());
    }

    @GetMapping("/exists/{phoneNumber}")
    public ResponseEntity<ApiResponse<Boolean>> existsByPhone(@PathVariable String phoneNumber) {

        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .success(true)
                .message("checked")
                .data(customerService.existsByPhoneNumber(phoneNumber))
                .build());
    }

    @GetMapping("/exists/{shopkeeperId}/{phoneNumber}")
    public ResponseEntity<ApiResponse<Boolean>> existsByShopkeeperAndPhone(
            @PathVariable Long shopkeeperId,
            @PathVariable String phoneNumber) {

        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .success(true)
                .message("checked")
                .data(customerService.existsByShopkeeperAndPhone(shopkeeperId, phoneNumber))
                .build());
    }

    @GetMapping("/default-shopkeeper/{phoneNumber}")
    public ResponseEntity<ApiResponse<Long>> getDefaultShopkeeper(@PathVariable String phoneNumber) {

        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .success(true)
                .message("Default shopkeeper fetched successfully")
                .data(customerService.getDefaultShopkeeper(phoneNumber))
                .build());
    }

    @PutMapping("/default-shopkeeper")
    public ResponseEntity<ApiResponse<String>> setDefaultShopkeeper(
            @Valid @RequestBody SetDefaultShopRequest request) {

        customerService.setDefaultShopkeeper(request);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Default shopkeeper updated")
                .data("OK")
                .build());
    }

    @GetMapping("/shopkeeper/{shopkeeperId}/search")
    public ResponseEntity<ApiResponse<?>> searchCustomers(
            @PathVariable Long shopkeeperId,
            @RequestParam String query) {

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Search results")
                .data(customerService.searchCustomers(shopkeeperId, query))
                .build());
    }
}
