package com.kaatha.report.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class ShopkeeperDashboardResponse {
    private long totalCustomers;
    private BigDecimal totalOutstanding;
    private BigDecimal todaysCollections;
    private long totalTransactions;
    private List<Object> recentTransactions;
    private List<Object> pendingPayments;
    private List<Object> notifications;
}
