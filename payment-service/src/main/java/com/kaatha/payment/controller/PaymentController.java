package com.kaatha.payment.controller;

import com.kaatha.payment.dto.request.CreateOrderRequest;
import com.kaatha.payment.dto.request.VerifyPaymentRequest;
import com.kaatha.payment.dto.response.ApiResponse;
import com.kaatha.payment.dto.response.PaymentResponse;
import com.kaatha.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<PaymentResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        return ResponseEntity.ok(ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment order created")
                .data(paymentService.createOrder(request))
                .build());
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(
            @RequestBody VerifyPaymentRequest request) {

        return ResponseEntity.ok(ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment verified")
                .data(paymentService.verifyPayment(request))
                .build());
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {

        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment fetched")
                .data(paymentService.getPayment(id))
                .build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getByCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(ApiResponse.<List<PaymentResponse>>builder()
                .success(true)
                .message("Payments fetched")
                .data(paymentService.getPaymentsByCustomer(customerId))
                .build());
    }
}
