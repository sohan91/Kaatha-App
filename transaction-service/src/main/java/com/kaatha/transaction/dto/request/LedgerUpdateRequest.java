package com.kaatha.transaction.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerUpdateRequest {
    private Long shopkeeperId;
    private Long customerId;
    private BigDecimal amount;
    private String transactionType; // PURCHASE or PAYMENT
}
