package com.kaatha.transaction.service;

import com.kaatha.transaction.dto.request.PaymentRequest;
import com.kaatha.transaction.dto.request.PurchaseRequest;
import com.kaatha.transaction.dto.response.TransactionResponse;

import java.util.List;
import java.util.Map;

public interface TransactionService {

    TransactionResponse recordPurchase(PurchaseRequest request);

    TransactionResponse recordPayment(PaymentRequest request);

    List<TransactionResponse> getTransactionsByCustomer(Long customerId);

    List<TransactionResponse> getTransactionsByShopkeeper(Long shopkeeperId);

    Map<String, Object> getTodayCollections(Long shopkeeperId);
}
