package com.kaatha.transaction.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GenerateInvoiceRequest {
    private Long shopkeeperId;
    private Long customerId;
    private Long transactionId;
    private String shopName;
    private String shopAddress;
    private String shopPhone;
    private String customerName;
    private String customerPhone;
    private String itemsJson;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal finalAmount;
    private BigDecimal amountPaid;
    private BigDecimal outstandingAmount;
    private String paymentMethod;
    private String paymentStatus;
}
