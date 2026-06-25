package com.kaatha.payment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PaymentResponse {

    private Long id;
    private Long shopkeeperId;
    private Long customerId;
    private String transactionIds;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private String razorpayKeyId;
}
