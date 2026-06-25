package com.kaatha.ledger.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerResponse {
    private Long id;
    private Long shopkeeperId;
    private Long customerId;
    private BigDecimal outstandingBalance;
    private LocalDateTime lastTransactionDate;
    private BigDecimal lastTransactionAmount;
    private String lastTransactionType;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
