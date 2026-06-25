package com.kaatha.ledger.service;

import com.kaatha.ledger.dto.request.LedgerUpdateRequest;
import com.kaatha.ledger.dto.response.LedgerResponse;

import java.util.List;

public interface LedgerService {
    LedgerResponse updateLedgerBalance(LedgerUpdateRequest request);
    LedgerResponse getLedger(Long shopkeeperId, Long customerId);
    List<LedgerResponse> getLedgersByCustomer(Long customerId);
    List<LedgerResponse> getLedgersByShopkeeper(Long shopkeeperId);
}
