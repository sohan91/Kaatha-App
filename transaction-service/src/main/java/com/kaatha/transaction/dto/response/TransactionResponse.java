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
    private LocalDateTime transactionDate;
    private String notes;
    private List<TransactionItemResponse> items;
    private LocalDateTime createdAt;
}
