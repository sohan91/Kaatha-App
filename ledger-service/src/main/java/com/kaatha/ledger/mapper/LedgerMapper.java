package com.kaatha.ledger.mapper;

import com.kaatha.ledger.dto.response.LedgerResponse;
import com.kaatha.ledger.entity.CustomerLedger;
import org.springframework.stereotype.Component;

@Component
public class LedgerMapper {

    public LedgerResponse toResponse(CustomerLedger ledger) {
        return LedgerResponse.builder()
                .id(ledger.getId())
                .shopkeeperId(ledger.getShopkeeperId())
                .customerId(ledger.getCustomerId())
                .outstandingBalance(ledger.getOutstandingBalance())
                .lastTransactionDate(ledger.getLastTransactionDate())
                .lastTransactionAmount(ledger.getLastTransactionAmount())
                .lastTransactionType(ledger.getLastTransactionType())
                .active(ledger.isActive())
                .createdAt(ledger.getCreatedAt())
                .updatedAt(ledger.getUpdatedAt())
                .build();
    }
}
