package com.kaatha.transaction.service.impl;

import com.kaatha.transaction.dto.request.*;
import com.kaatha.transaction.dto.response.*;
import com.kaatha.transaction.entity.Transaction;
import com.kaatha.transaction.entity.TransactionItem;
import com.kaatha.transaction.feign.ItemClient;
import com.kaatha.transaction.feign.LedgerClient;
import com.kaatha.transaction.feign.NotificationClient;
import com.kaatha.transaction.mapper.TransactionMapper;
import com.kaatha.transaction.repository.TransactionItemRepository;
import com.kaatha.transaction.repository.TransactionRepository;
import com.kaatha.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository itemRepository;
    private final TransactionMapper transactionMapper;

    private final ItemClient itemClient;
    private final LedgerClient ledgerClient;
    private final NotificationClient notificationClient;

    @Override
    @Transactional
    public TransactionResponse recordPurchase(PurchaseRequest request) {
        List<Long> itemIds = request.getItems().stream()
                .map(PurchaseItemRequest::getItemId)
                .toList();

        // 1. Fetch item details from Item Service
        ApiResponse<List<ItemResponse>> itemDetailsRes = itemClient.getItemsByIds(itemIds);
        if (itemDetailsRes == null || !itemDetailsRes.isSuccess() || itemDetailsRes.getData() == null) {
            throw new RuntimeException("Could not retrieve items from Item Service");
        }

        Map<Long, ItemResponse> itemMap = itemDetailsRes.getData().stream()
                .collect(Collectors.toMap(ItemResponse::getId, item -> item));

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<StockUpdateRequest> stockUpdates = new ArrayList<>();
        List<TransactionItem> transactionItems = new ArrayList<>();

        // 2. Validate items and compute subtotals
        for (PurchaseItemRequest itemReq : request.getItems()) {
            ItemResponse item = itemMap.get(itemReq.getItemId());
            if (item == null) {
                throw new RuntimeException("Item not found: ID " + itemReq.getItemId());
            }
            if (!item.isActive()) {
                throw new RuntimeException("Item is inactive: " + item.getName());
            }
            if (!item.getShopkeeperId().equals(request.getShopkeeperId())) {
                throw new RuntimeException("Item " + item.getName() + " does not belong to shopkeeper: " + request.getShopkeeperId());
            }
            if (item.getStockQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException("Insufficient stock for item: " + item.getName() +
                        ". Requested: " + itemReq.getQuantity() + ", Available: " + item.getStockQuantity());
            }

            BigDecimal unitPrice = item.getPrice();
            BigDecimal discount = itemReq.getDiscount() != null ? itemReq.getDiscount() : BigDecimal.ZERO;
            BigDecimal finalPrice = unitPrice.subtract(discount);
            if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
                finalPrice = BigDecimal.ZERO;
            }
            BigDecimal subtotal = finalPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            // Record transaction item details
            transactionItems.add(TransactionItem.builder()
                    .itemId(item.getId())
                    .itemName(item.getName())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(unitPrice)
                    .discount(discount)
                    .subtotal(subtotal)
                    .build());

            // Prepare stock update request
            stockUpdates.add(StockUpdateRequest.builder()
                    .itemId(item.getId())
                    .quantityChange(-itemReq.getQuantity()) // deduct stock
                    .build());
        }

        // 3. Save parent transaction
        Transaction transaction = Transaction.builder()
                .shopkeeperId(request.getShopkeeperId())
                .customerId(request.getCustomerId())
                .amount(totalAmount)
                .type("PURCHASE")
                .transactionDate(LocalDateTime.now())
                .notes(request.getNotes())
                .build();
        Transaction savedTx = transactionRepository.save(transaction);

        // 4. Save transaction items linked to parent
        for (TransactionItem txItem : transactionItems) {
            txItem.setTransactionId(savedTx.getId());
        }
        List<TransactionItem> savedItems = itemRepository.saveAll(transactionItems);

        // 5. Update stock in Item Service
        itemClient.updateStock(stockUpdates);

        // 6. Update ledger balance in Ledger Service
        ledgerClient.updateLedgerBalance(LedgerUpdateRequest.builder()
                .shopkeeperId(request.getShopkeeperId())
                .customerId(request.getCustomerId())
                .amount(totalAmount)
                .transactionType("PURCHASE")
                .build());

        // 7. Send notification (async/non-blocking for main transaction flow)
        try {
            notificationClient.sendNotification(NotificationRequest.builder()
                    .recipientPhone("customer-phone") // in a real app, resolve customer phone first
                    .message("You made a purchase on credit of Rs. " + totalAmount + " at Shop ID " + request.getShopkeeperId())
                    .type("PAYMENT")
                    .build());
        } catch (Exception e) {
            log.warn("Failed to send purchase notification", e);
        }

        return transactionMapper.toResponse(savedTx, savedItems);
    }

    @Override
    @Transactional
    public TransactionResponse recordPayment(PaymentRequest request) {
        // 1. Save transaction
        Transaction transaction = Transaction.builder()
                .shopkeeperId(request.getShopkeeperId())
                .customerId(request.getCustomerId())
                .amount(request.getAmount())
                .type("PAYMENT")
                .transactionDate(LocalDateTime.now())
                .notes(request.getNotes())
                .build();
        Transaction savedTx = transactionRepository.save(transaction);

        // 2. Update ledger balance in Ledger Service
        ledgerClient.updateLedgerBalance(LedgerUpdateRequest.builder()
                .shopkeeperId(request.getShopkeeperId())
                .customerId(request.getCustomerId())
                .amount(request.getAmount())
                .transactionType("PAYMENT")
                .build());

        // 3. Send notification
        try {
            notificationClient.sendNotification(NotificationRequest.builder()
                    .recipientPhone("customer-phone")
                    .message("Thank you! Payment of Rs. " + request.getAmount() + " received at Shop ID " + request.getShopkeeperId())
                    .type("PAYMENT")
                    .build());
        } catch (Exception e) {
            log.warn("Failed to send payment notification", e);
        }

        return transactionMapper.toResponse(savedTx, null);
    }

    @Override
    public List<TransactionResponse> getTransactionsByCustomer(Long customerId) {
        return transactionRepository.findByCustomerIdOrderByTransactionDateDesc(customerId)
                .stream()
                .map(tx -> {
                    List<TransactionItem> items = null;
                    if ("PURCHASE".equals(tx.getType())) {
                        items = itemRepository.findByTransactionId(tx.getId());
                    }
                    return transactionMapper.toResponse(tx, items);
                })
                .toList();
    }

    @Override
    public List<TransactionResponse> getTransactionsByShopkeeper(Long shopkeeperId) {
        return transactionRepository.findByShopkeeperIdOrderByTransactionDateDesc(shopkeeperId)
                .stream()
                .map(tx -> {
                    List<TransactionItem> items = null;
                    if ("PURCHASE".equals(tx.getType())) {
                        items = itemRepository.findByTransactionId(tx.getId());
                    }
                    return transactionMapper.toResponse(tx, items);
                })
                .toList();
    }
}
