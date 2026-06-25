package com.kaatha.ledger.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerUpdateRequest {

    @NotNull(message = "Shopkeeper ID is required")
    private Long shopkeeperId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Transaction amount is required")
    private BigDecimal amount;

    @NotBlank(message = "Transaction type is required")
    private String transactionType;
}
