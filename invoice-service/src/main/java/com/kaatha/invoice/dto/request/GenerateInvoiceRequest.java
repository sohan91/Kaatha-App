package com.kaatha.invoice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GenerateInvoiceRequest {

    @NotNull
    private Long shopkeeperId;

    @NotNull
    private Long customerId;

    @NotNull
    private Long transactionId;

    @NotBlank
    private String shopName;

    private String shopAddress;
    private String shopPhone;

    @NotBlank
    private String customerName;

    @NotBlank
    private String customerPhone;

    @NotBlank
    private String itemsJson;

    @NotNull
    private BigDecimal subtotal;

    private BigDecimal tax;
    private BigDecimal discount;

    @NotNull
    private BigDecimal finalAmount;

    private BigDecimal amountPaid;
    private BigDecimal outstandingAmount;
    private String paymentMethod;
    private String paymentStatus;
}
