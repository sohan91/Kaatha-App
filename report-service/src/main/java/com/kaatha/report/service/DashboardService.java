package com.kaatha.report.service;

import com.kaatha.report.dto.response.CustomerDashboardResponse;
import com.kaatha.report.dto.response.ShopkeeperDashboardResponse;
import com.kaatha.report.dto.response.ApiResponse;
import com.kaatha.report.feign.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CustomerClient customerClient;
    private final LedgerClient ledgerClient;
    private final TransactionClient transactionClient;
    private final NotificationClient notificationClient;
    private final InvoiceClient invoiceClient;

    public ShopkeeperDashboardResponse getShopkeeperDashboard(Long shopkeeperId) {
        List<Map<String, Object>> customers = safeList(customerClient.getCustomers(shopkeeperId));
        List<Map<String, Object>> ledgers = safeList(ledgerClient.getLedgersByShopkeeper(shopkeeperId));
        List<Map<String, Object>> transactions = safeList(transactionClient.getByShopkeeper(shopkeeperId));

        BigDecimal totalOutstanding = ledgers.stream()
                .map(l -> toBigDecimal(l.get("outstandingBalance")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal todaysCollections = BigDecimal.ZERO;
        ApiResponse<Map<String, Object>> todayRes = transactionClient.getTodayCollections(shopkeeperId);
        if (todayRes != null && todayRes.getData() != null) {
            todaysCollections = toBigDecimal(todayRes.getData().get("totalCollections"));
        }

        List<Object> pending = transactions.stream()
                .filter(t -> "PENDING".equals(String.valueOf(t.get("paymentStatus"))))
                .limit(10)
                .map(t -> (Object) t)
                .toList();

        List<Object> recent = transactions.stream().limit(10).map(t -> (Object) t).toList();

        return ShopkeeperDashboardResponse.builder()
                .totalCustomers(customers.size())
                .totalOutstanding(totalOutstanding)
                .todaysCollections(todaysCollections)
                .totalTransactions(transactions.size())
                .recentTransactions(recent)
                .pendingPayments(pending)
                .notifications(List.of())
                .build();
    }

    public CustomerDashboardResponse getCustomerDashboard(String phoneNumber) {
        List<Map<String, Object>> shops = safeList(customerClient.getShopsForPhone(phoneNumber));

        List<CustomerDashboardResponse.ShopOutstanding> outstanding = new ArrayList<>();
        List<Object> recentPurchases = new ArrayList<>();
        List<Object> invoices = new ArrayList<>();

        for (Map<String, Object> shop : shops) {
            Long customerId = toLong(shop.get("customerId"));
            Long shopkeeperId = toLong(shop.get("shopkeeperId"));

            List<Map<String, Object>> ledgers = safeList(ledgerClient.getLedgersByCustomer(customerId));
            for (Map<String, Object> ledger : ledgers) {
                if (shopkeeperId.equals(toLong(ledger.get("shopkeeperId")))) {
                    outstanding.add(CustomerDashboardResponse.ShopOutstanding.builder()
                            .shopkeeperId(shopkeeperId)
                            .customerId(customerId)
                            .outstandingBalance(toBigDecimal(ledger.get("outstandingBalance")))
                            .build());
                }
            }

            recentPurchases.addAll(safeList(transactionClient.getByCustomer(customerId)).stream()
                    .limit(5)
                    .map(t -> (Object) t)
                    .toList());

            invoices.addAll(safeList(invoiceClient.getByCustomer(customerId)).stream()
                    .limit(5)
                    .map(i -> (Object) i)
                    .toList());
        }

        List<Object> notifications = safeList(notificationClient.getByPhone(phone)).stream()
                .map(n -> (Object) n)
                .toList();

        Object profile = shops.isEmpty() ? null : shops.getFirst();

        return CustomerDashboardResponse.builder()
                .profile(profile)
                .shopkeepers(shops.stream().map(s -> (Object) s).toList())
                .outstandingPerShop(outstanding)
                .recentPurchases(recentPurchases.stream().limit(10).toList())
                .pendingPayments(recentPurchases.stream()
                        .filter(p -> p instanceof Map && "PENDING".equals(
                                String.valueOf(((Map<?, ?>) p).get("paymentStatus"))))
                        .limit(10)
                        .toList())
                .notifications(notifications)
                .invoices(invoices.stream().limit(10).toList())
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> safeList(ApiResponse<?> response) {
        if (response == null || response.getData() == null) {
            return List.of();
        }
        return (List<Map<String, Object>>) response.getData();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.toString());
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(value.toString());
    }
}
