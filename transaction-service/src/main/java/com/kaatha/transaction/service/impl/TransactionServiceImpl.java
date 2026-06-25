package com.kaatha.transaction.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaatha.transaction.dto.request.*;
import com.kaatha.transaction.dto.response.*;
import com.kaatha.transaction.entity.Transaction;
import com.kaatha.transaction.entity.TransactionItem;
import com.kaatha.transaction.feign.*;
import com.kaatha.transaction.mapper.TransactionMapper;
import com.kaatha.transaction.repository.TransactionItemRepository;
import com.kaatha.transaction.repository.TransactionRepository;
import com.kaatha.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
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
    private final CustomerClient customerClient;
    private final ShopkeeperClient shopkeeperClient;
    private final InvoiceClient invoiceClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public TransactionResponse recordPurchase(PurchaseRequest request) {
        List<Long> itemIds = request.getItems().stream()
                .map(PurchaseItemRequest::getItemId)
                .toList();

        ApiResponse<List<ItemResponse>> itemDetailsRes = itemClient.getItemsByIds(itemIds);
        if (itemDetailsRes == null || !itemDetailsRes.isSuccess() || itemDetailsRes.getData() == null) {
            throw new RuntimeException("Could not retrieve items from Item Service");
        }

        Map<Long, ItemResponse> itemMap = itemDetailsRes.getData().stream()
                .collect(Collectors.toMap(ItemResponse::getId, item -> item));

        BigDecimal subtotal = BigDecimal.ZERO;
        List<StockUpdateRequest> stockUpdates = new ArrayList<>();
        List<TransactionItem> transactionItems = new ArrayList<>();

        for (PurchaseItemRequest itemReq : request.getItems()) {
            ItemResponse item = itemMap.get(itemReq.getItemId());
            validateItem(item, itemReq, request.getShopkeeperId());

            BigDecimal unitPrice = item.getPrice();
            BigDecimal discount = itemReq.getDiscount() != null ? itemReq.getDiscount() : BigDecimal.ZERO;
            BigDecimal finalPrice = unitPrice.subtract(discount).max(BigDecimal.ZERO);
            BigDecimal lineSubtotal = finalPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            subtotal = subtotal.add(lineSubtotal);

            transactionItems.add(TransactionItem.builder()
                    .itemId(item.getId())
                    .itemName(item.getName())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(unitPrice)
                    .discount(discount)
                    .subtotal(lineSubtotal)
                    .build());

            stockUpdates.add(StockUpdateRequest.builder()
                    .itemId(item.getId())
                    .quantityChange(-itemReq.getQuantity())
                    .build());
        }

        BigDecimal tax = request.getTax() != null ? request.getTax() : BigDecimal.ZERO;
        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        BigDecimal finalAmount = subtotal.add(tax).subtract(discount).max(BigDecimal.ZERO);

        String paymentStatus = request.getPaymentStatus() != null ? request.getPaymentStatus() : "PENDING";
        String paymentMethod = request.getPaymentMethod() != null ? request.getPaymentMethod() : "CASH";
        BigDecimal amountPaid = "PAID".equals(paymentStatus)
                ? finalAmount
                : (request.getAmountPaid() != null ? request.getAmountPaid() : BigDecimal.ZERO);
        BigDecimal outstanding = finalAmount.subtract(amountPaid).max(BigDecimal.ZERO);

        Transaction transaction = Transaction.builder()
                .shopkeeperId(request.getShopkeeperId())
                .customerId(request.getCustomerId())
                .amount(finalAmount)
                .type("PURCHASE")
                .transactionNumber(generateTransactionNumber())
                .subtotal(subtotal)
                .tax(tax)
                .discount(discount)
                .finalAmount(finalAmount)
                .amountPaid(amountPaid)
                .outstandingAmount(outstanding)
                .paymentStatus(paymentStatus)
                .paymentMethod(paymentMethod)
                .transactionDate(LocalDateTime.now())
                .notes(request.getNotes())
                .build();
        Transaction savedTx = transactionRepository.save(transaction);

        for (TransactionItem txItem : transactionItems) {
            txItem.setTransactionId(savedTx.getId());
        }
        List<TransactionItem> savedItems = itemRepository.saveAll(transactionItems);

        itemClient.updateStock(stockUpdates);

        if (outstanding.compareTo(BigDecimal.ZERO) > 0) {
            ledgerClient.updateLedgerBalance(LedgerUpdateRequest.builder()
                    .shopkeeperId(request.getShopkeeperId())
                    .customerId(request.getCustomerId())
                    .amount(outstanding)
                    .transactionType("PURCHASE")
                    .build());
        }

        String customerPhone = resolveCustomerPhone(request.getCustomerId());
        generateAndNotify(savedTx, savedItems, customerPhone);

        return transactionMapper.toResponse(savedTx, savedItems);
    }

    @Override
    @Transactional
    public TransactionResponse recordPayment(PaymentRequest request) {
        Transaction transaction = Transaction.builder()
                .shopkeeperId(request.getShopkeeperId())
                .customerId(request.getCustomerId())
                .amount(request.getAmount())
                .type("PAYMENT")
                .transactionNumber(generateTransactionNumber())
                .subtotal(request.getAmount())
                .tax(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .finalAmount(request.getAmount())
                .amountPaid(request.getAmount())
                .outstandingAmount(BigDecimal.ZERO)
                .paymentStatus("PAID")
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CASH")
                .transactionDate(LocalDateTime.now())
                .notes(request.getNotes())
                .build();
        Transaction savedTx = transactionRepository.save(transaction);

        ledgerClient.updateLedgerBalance(LedgerUpdateRequest.builder()
                .shopkeeperId(request.getShopkeeperId())
                .customerId(request.getCustomerId())
                .amount(request.getAmount())
                .transactionType("PAYMENT")
                .build());

        String customerPhone = resolveCustomerPhone(request.getCustomerId());
        generateAndNotify(savedTx, null, customerPhone);

        return transactionMapper.toResponse(savedTx, null);
    }

    @Override
    public List<TransactionResponse> getTransactionsByCustomer(Long customerId) {
        return mapTransactions(transactionRepository.findByCustomerIdOrderByTransactionDateDesc(customerId));
    }

    @Override
    public List<TransactionResponse> getTransactionsByShopkeeper(Long shopkeeperId) {
        return mapTransactions(transactionRepository.findByShopkeeperIdOrderByTransactionDateDesc(shopkeeperId));
    }

    @Override
    public Map<String, Object> getTodayCollections(Long shopkeeperId) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        BigDecimal total = transactionRepository.sumTodayCollections(shopkeeperId, start, end);
        return Map.of("shopkeeperId", shopkeeperId, "totalCollections", total);
    }

    private List<TransactionResponse> mapTransactions(List<Transaction> transactions) {
        return transactions.stream()
                .map(tx -> {
                    List<TransactionItem> items = "PURCHASE".equals(tx.getType())
                            ? itemRepository.findByTransactionId(tx.getId())
                            : null;
                    return transactionMapper.toResponse(tx, items);
                })
                .toList();
    }

    private void validateItem(ItemResponse item, PurchaseItemRequest itemReq, Long shopkeeperId) {
        if (item == null) {
            throw new RuntimeException("Item not found: ID " + itemReq.getItemId());
        }
        if (!item.isActive()) {
            throw new RuntimeException("Item is inactive: " + item.getName());
        }
        if (!item.getShopkeeperId().equals(shopkeeperId)) {
            throw new RuntimeException("Item does not belong to shopkeeper: " + shopkeeperId);
        }
        if (item.getStockQuantity() < itemReq.getQuantity()) {
            throw new RuntimeException("Insufficient stock for item: " + item.getName());
        }
    }

    private String generateTransactionNumber() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    private String resolveCustomerPhone(Long customerId) {
        try {
            ApiResponse<Map<String, Object>> res = customerClient.getCustomer(customerId);
            if (res != null && res.getData() != null) {
                return String.valueOf(res.getData().get("phoneNumber"));
            }
        } catch (Exception e) {
            log.warn("Could not resolve customer phone for id {}", customerId);
        }
        return null;
    }

    private void generateAndNotify(Transaction savedTx, List<TransactionItem> items, String customerPhone) {
        try {
            Map<String, Object> shopkeeper = Optional.ofNullable(
                    shopkeeperClient.getShopkeeper(savedTx.getShopkeeperId()))
                    .map(ApiResponse::getData)
                    .orElse(Map.of());

            Map<String, Object> customer = Optional.ofNullable(
                    customerClient.getCustomer(savedTx.getCustomerId()))
                    .map(ApiResponse::getData)
                    .orElse(Map.of());

            String itemsJson = items != null
                    ? objectMapper.writeValueAsString(items.stream().map(i -> Map.of(
                    "name", i.getItemName(),
                    "qty", i.getQuantity(),
                    "price", i.getUnitPrice())).toList())
                    : "[]";

            GenerateInvoiceRequest invoiceReq = new GenerateInvoiceRequest();
            invoiceReq.setShopkeeperId(savedTx.getShopkeeperId());
            invoiceReq.setCustomerId(savedTx.getCustomerId());
            invoiceReq.setTransactionId(savedTx.getId());
            invoiceReq.setShopName(String.valueOf(shopkeeper.getOrDefault("shopName", "Shop")));
            invoiceReq.setShopAddress(buildAddress(shopkeeper));
            invoiceReq.setShopPhone(String.valueOf(shopkeeper.getOrDefault("phoneNumber", "")));
            invoiceReq.setCustomerName(customer.get("firstName") + " " + customer.getOrDefault("lastName", ""));
            invoiceReq.setCustomerPhone(String.valueOf(customer.getOrDefault("phoneNumber", customerPhone)));
            invoiceReq.setItemsJson(itemsJson);
            invoiceReq.setSubtotal(savedTx.getSubtotal());
            invoiceReq.setTax(savedTx.getTax());
            invoiceReq.setDiscount(savedTx.getDiscount());
            invoiceReq.setFinalAmount(savedTx.getFinalAmount());
            invoiceReq.setAmountPaid(savedTx.getAmountPaid());
            invoiceReq.setOutstandingAmount(savedTx.getOutstandingAmount());
            invoiceReq.setPaymentMethod(savedTx.getPaymentMethod());
            invoiceReq.setPaymentStatus(savedTx.getPaymentStatus());

            ApiResponse<Map<String, Object>> invoiceRes = invoiceClient.generateInvoice(invoiceReq);
            if (invoiceRes != null && invoiceRes.getData() != null) {
                savedTx.setInvoiceNumber(String.valueOf(invoiceRes.getData().get("invoiceNumber")));
                transactionRepository.save(savedTx);
            }

            String message = "Purchase".equals(savedTx.getType())
                    ? "New purchase of Rs. " + savedTx.getFinalAmount() + ". Status: " + savedTx.getPaymentStatus()
                    : "Payment of Rs. " + savedTx.getAmount() + " received. Thank you!";

            if (customerPhone != null) {
                notificationClient.sendNotification(NotificationRequest.builder()
                        .recipientPhone(customerPhone)
                        .message(message)
                        .type("PAYMENT")
                        .build());
            }

            String shopPhone = String.valueOf(shopkeeper.getOrDefault("phoneNumber", ""));
            if (!shopPhone.isBlank()) {
                notificationClient.sendNotification(NotificationRequest.builder()
                        .recipientPhone(shopPhone)
                        .message(message)
                        .type("PAYMENT")
                        .build());
            }
        } catch (Exception e) {
            log.warn("Post-transaction invoice/notification failed", e);
        }
    }

    private String buildAddress(Map<String, Object> shopkeeper) {
        return String.join(", ",
                String.valueOf(shopkeeper.getOrDefault("addressLine1", "")),
                String.valueOf(shopkeeper.getOrDefault("city", "")),
                String.valueOf(shopkeeper.getOrDefault("state", "")),
                String.valueOf(shopkeeper.getOrDefault("pincode", "")));
    }
}
