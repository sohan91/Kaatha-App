package com.kaatha.transaction.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long shopkeeperId;
    private Long customerId;
    private BigDecimal amount;
    private String type;
    private String transactionNumber;
    private String invoiceNumber;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal finalAmount;
    private BigDecimal amountPaid;
    private BigDecimal outstandingAmount;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime transactionDate;
    private String notes;
    private List<TransactionItemResponse> items;
    private LocalDateTime createdAt;
}
