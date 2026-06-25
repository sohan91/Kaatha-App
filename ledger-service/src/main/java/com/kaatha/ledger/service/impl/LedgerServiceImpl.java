package com.kaatha.ledger.service.impl;

import com.kaatha.ledger.dto.request.LedgerUpdateRequest;
import com.kaatha.ledger.dto.response.LedgerResponse;
import com.kaatha.ledger.entity.CustomerLedger;
import com.kaatha.ledger.exception.LedgerNotFoundException;
import com.kaatha.ledger.mapper.LedgerMapper;
import com.kaatha.ledger.repository.CustomerLedgerRepository;
import com.kaatha.ledger.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

    private final CustomerLedgerRepository ledgerRepository;
    private final LedgerMapper ledgerMapper;

    @Override
    @Transactional
    public LedgerResponse updateLedgerBalance(LedgerUpdateRequest request) {
        CustomerLedger ledger = ledgerRepository
                .findByShopkeeperIdAndCustomerId(request.getShopkeeperId(), request.getCustomerId())
                .orElseGet(() -> CustomerLedger.builder()
                        .shopkeeperId(request.getShopkeeperId())
                        .customerId(request.getCustomerId())
                        .outstandingBalance(BigDecimal.ZERO)
                        .active(true)
                        .build());

        BigDecimal currentBalance = ledger.getOutstandingBalance();
        BigDecimal updateAmount = request.getAmount();

        if ("PURCHASE".equalsIgnoreCase(request.getTransactionType())) {
            ledger.setOutstandingBalance(currentBalance.add(updateAmount));
        } else if ("PAYMENT".equalsIgnoreCase(request.getTransactionType())) {
            ledger.setOutstandingBalance(currentBalance.subtract(updateAmount));
        } else {
            throw new IllegalArgumentException("Invalid transaction type: " + request.getTransactionType());
        }

        ledger.setLastTransactionAmount(updateAmount);
        ledger.setLastTransactionType(request.getTransactionType().toUpperCase());
        ledger.setLastTransactionDate(LocalDateTime.now());

        CustomerLedger savedLedger = ledgerRepository.save(ledger);
        return ledgerMapper.toResponse(savedLedger);
    }

    @Override
    public LedgerResponse getLedger(Long shopkeeperId, Long customerId) {
        CustomerLedger ledger = ledgerRepository
                .findByShopkeeperIdAndCustomerId(shopkeeperId, customerId)
                .orElseThrow(() -> new LedgerNotFoundException("Ledger not found for shopkeeper: " +
                        shopkeeperId + ", customer: " + customerId));
        return ledgerMapper.toResponse(ledger);
    }

    @Override
    public List<LedgerResponse> getLedgersByCustomer(Long customerId) {
        return ledgerRepository.findByCustomerId(customerId)
                .stream()
                .map(ledgerMapper::toResponse)
                .toList();
    }

    @Override
    public List<LedgerResponse> getLedgersByShopkeeper(Long shopkeeperId) {
        return ledgerRepository.findByShopkeeperId(shopkeeperId)
                .stream()
                .map(ledgerMapper::toResponse)
                .toList();
    }
}
