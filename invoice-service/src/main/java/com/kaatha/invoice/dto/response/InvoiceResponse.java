package com.kaatha.invoice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
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
    private LocalDateTime invoiceDate;
}
