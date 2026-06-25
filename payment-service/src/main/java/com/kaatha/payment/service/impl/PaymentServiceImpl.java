package com.kaatha.payment.service.impl;

import com.kaatha.payment.dto.request.CreateOrderRequest;
import com.kaatha.payment.dto.request.VerifyPaymentRequest;
import com.kaatha.payment.dto.response.ApiResponse;
import com.kaatha.payment.dto.response.PaymentResponse;
import com.kaatha.payment.dto.response.ShopkeeperResponse;
import com.kaatha.payment.entity.PaymentRecord;
import com.kaatha.payment.feign.ShopkeeperClient;
import com.kaatha.payment.repository.PaymentRecordRepository;
import com.kaatha.payment.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRecordRepository paymentRepository;
    private final ShopkeeperClient shopkeeperClient;

    @Value("${razorpay.key-id:}")
    private String keyId;

    @Value("${razorpay.key-secret:}")
    private String keySecret;

    @Value("${razorpay.webhook-secret:}")
    private String webhookSecret;

    @Override
    public PaymentResponse createOrder(CreateOrderRequest request) {
        ApiResponse<ShopkeeperResponse> shopkeeperRes = shopkeeperClient.getShopkeeper(request.getShopkeeperId());
        if (shopkeeperRes == null || shopkeeperRes.getData() == null) {
            throw new IllegalArgumentException("Shopkeeper not found");
        }

        String orderId;
        if (isRazorpayConfigured()) {
            try {
                RazorpayClient client = new RazorpayClient(keyId, keySecret);
                JSONObject options = new JSONObject();
                options.put("amount", request.getAmount().multiply(java.math.BigDecimal.valueOf(100)).intValue());
                options.put("currency", "INR");
                options.put("receipt", "rcpt_" + UUID.randomUUID().toString().substring(0, 8));

                JSONObject transfer = new JSONObject();
                transfer.put("account", shopkeeperRes.getData().getRazorpayAccountId());
                transfer.put("amount", options.getInt("amount"));
                transfer.put("currency", "INR");
                options.put("transfers", new org.json.JSONArray().put(transfer));

                Order order = client.orders.create(options);
                orderId = order.get("id");
            } catch (RazorpayException e) {
                log.error("Razorpay order creation failed", e);
                throw new RuntimeException("Payment gateway error: " + e.getMessage());
            }
        } else {
            orderId = "order_mock_" + UUID.randomUUID().toString().substring(0, 12);
            log.info("Mock Razorpay order created: {}", orderId);
        }

        PaymentRecord record = paymentRepository.save(PaymentRecord.builder()
                .shopkeeperId(request.getShopkeeperId())
                .customerId(request.getCustomerId())
                .transactionIds(request.getTransactionIds().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")))
                .razorpayOrderId(orderId)
                .amount(request.getAmount())
                .status("CREATED")
                .paymentMethod("RAZORPAY")
                .build());

        return toResponse(record);
    }

    @Override
    public PaymentResponse verifyPayment(VerifyPaymentRequest request) {
        PaymentRecord record = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Payment order not found"));

        if (isRazorpayConfigured()) {
            try {
                JSONObject attributes = new JSONObject();
                attributes.put("razorpay_order_id", request.getRazorpayOrderId());
                attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
                attributes.put("razorpay_signature", request.getRazorpaySignature());
                if (!Utils.verifyPaymentSignature(attributes, keySecret)) {
                    record.setStatus("FAILED");
                    paymentRepository.save(record);
                    throw new IllegalArgumentException("Invalid payment signature");
                }
            } catch (RazorpayException e) {
                throw new RuntimeException("Payment verification failed");
            }
        }

        record.setRazorpayPaymentId(request.getRazorpayPaymentId());
        record.setStatus("SUCCESS");
        return toResponse(paymentRepository.save(record));
    }

    @Override
    public PaymentResponse getPayment(Long id) {
        return toResponse(paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found")));
    }

    @Override
    public List<PaymentResponse> getPaymentsByCustomer(Long customerId) {
        return paymentRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void handleWebhook(String payload, String signature) {
        if (webhookSecret != null && !webhookSecret.isBlank()) {
            String expected = hmacSha256(payload, webhookSecret);
            if (!expected.equals(signature)) {
                throw new IllegalArgumentException("Invalid webhook signature");
            }
        }
        JSONObject event = new JSONObject(payload);
        if ("payment.captured".equals(event.optString("event"))) {
            JSONObject payment = event.getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity");
            String orderId = payment.optString("order_id");
            paymentRepository.findByRazorpayOrderId(orderId).ifPresent(record -> {
                record.setRazorpayPaymentId(payment.optString("id"));
                record.setStatus("SUCCESS");
                paymentRepository.save(record);
            });
        }
    }

    private PaymentResponse toResponse(PaymentRecord record) {
        return PaymentResponse.builder()
                .id(record.getId())
                .shopkeeperId(record.getShopkeeperId())
                .customerId(record.getCustomerId())
                .transactionIds(record.getTransactionIds())
                .razorpayOrderId(record.getRazorpayOrderId())
                .razorpayPaymentId(record.getRazorpayPaymentId())
                .amount(record.getAmount())
                .status(record.getStatus())
                .paymentMethod(record.getPaymentMethod())
                .createdAt(record.getCreatedAt())
                .razorpayKeyId(isRazorpayConfigured() ? keyId : "mock_key")
                .build();
    }

    private boolean isRazorpayConfigured() {
        return keyId != null && !keyId.isBlank() && keySecret != null && !keySecret.isBlank();
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("HMAC computation failed", e);
        }
    }
}
