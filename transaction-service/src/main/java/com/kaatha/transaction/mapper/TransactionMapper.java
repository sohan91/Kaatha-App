package com.kaatha.transaction.mapper;

import com.kaatha.transaction.dto.response.TransactionItemResponse;
import com.kaatha.transaction.dto.response.TransactionResponse;
import com.kaatha.transaction.entity.Transaction;
import com.kaatha.transaction.entity.TransactionItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction, List<TransactionItem> items) {
        List<TransactionItemResponse> itemResponses = null;
        if (items != null) {
            itemResponses = items.stream().map(this::toResponse).toList();
        }

        return TransactionResponse.builder()
                .id(transaction.getId())
                .shopkeeperId(transaction.getShopkeeperId())
                .customerId(transaction.getCustomerId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .transactionNumber(transaction.getTransactionNumber())
                .invoiceNumber(transaction.getInvoiceNumber())
                .subtotal(transaction.getSubtotal())
                .tax(transaction.getTax())
                .discount(transaction.getDiscount())
                .finalAmount(transaction.getFinalAmount())
                .amountPaid(transaction.getAmountPaid())
                .outstandingAmount(transaction.getOutstandingAmount())
                .paymentStatus(transaction.getPaymentStatus())
                .paymentMethod(transaction.getPaymentMethod())
                .transactionDate(transaction.getTransactionDate())
                .notes(transaction.getNotes())
                .items(itemResponses)
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    public TransactionItemResponse toResponse(TransactionItem item) {
        return TransactionItemResponse.builder()
                .id(item.getId())
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .discount(item.getDiscount())
                .subtotal(item.getSubtotal())
                .build();
    }
}
