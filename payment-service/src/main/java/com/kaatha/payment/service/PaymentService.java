package com.kaatha.payment.service;

import com.kaatha.payment.dto.request.CreateOrderRequest;
import com.kaatha.payment.dto.request.VerifyPaymentRequest;
import com.kaatha.payment.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse createOrder(CreateOrderRequest request);

    PaymentResponse verifyPayment(VerifyPaymentRequest request);

    PaymentResponse getPayment(Long id);

    List<PaymentResponse> getPaymentsByCustomer(Long customerId);

    void handleWebhook(String payload, String signature);
}
