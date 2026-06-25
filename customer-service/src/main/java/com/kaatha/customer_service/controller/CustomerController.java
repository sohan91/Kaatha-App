package com.kaatha.customer_service.controller;

import com.kaatha.customer_service.dto.request.RegisterCustomerRequest;
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

        CustomerResponse response =
                customerService.registerCustomer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<CustomerResponse>builder()
                                .success(true)
                                .message("Customer processed successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
            @PathVariable Long customerId) {

        CustomerResponse response =
                customerService.getCustomerById(customerId);

        return ResponseEntity.ok(
                ApiResponse.<CustomerResponse>builder()
                        .success(true)
                        .message("Customer fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getByPhone(
            @PathVariable String phoneNumber) {

        CustomerResponse response =
                customerService.getCustomerByPhone(phoneNumber);

        return ResponseEntity.ok(
                ApiResponse.<CustomerResponse>builder()
                        .success(true)
                        .message("Customer fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/shopkeeper/{shopkeeperId}")
    public ResponseEntity<ApiResponse<?>> getByShopkeeper(
            @PathVariable Long shopkeeperId) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Customers fetched successfully")
                        .data(customerService.getCustomersByShopkeeper(shopkeeperId))
                        .build()
        );
    }

    @GetMapping("/exists/{phoneNumber}")
    public ResponseEntity<ApiResponse<Boolean>> existsByPhone(
            @PathVariable String phoneNumber) {

        boolean exists = customerService.existsByPhoneNumber(phoneNumber);

        return ResponseEntity.ok(
                ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("checked")
                        .data(exists)
                        .build()
        );
    }
    @GetMapping("/default-shopkeeper/{phoneNumber}")
    public ResponseEntity<ApiResponse<Long>> getDefaultShopkeeper(
            @PathVariable String phoneNumber) {

        Long shopkeeperId =
                customerService.getDefaultShopkeeper(phoneNumber);

        return ResponseEntity.ok(
                ApiResponse.<Long>builder()
                        .success(true)
                        .message("Default shopkeeper fetched successfully")
                        .data(shopkeeperId)
                        .build()
        );
    }
}