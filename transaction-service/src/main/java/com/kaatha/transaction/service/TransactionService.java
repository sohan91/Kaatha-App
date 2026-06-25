package com.kaatha.transaction.service;

import com.kaatha.transaction.dto.request.PaymentRequest;
import com.kaatha.transaction.dto.request.PurchaseRequest;
import com.kaatha.transaction.dto.response.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse recordPurchase(PurchaseRequest request);
    TransactionResponse recordPayment(PaymentRequest request);
    List<TransactionResponse> getTransactionsByCustomer(Long customerId);
    List<TransactionResponse> getTransactionsByShopkeeper(Long shopkeeperId);
}
