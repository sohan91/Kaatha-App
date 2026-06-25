package com.kaatha.report.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class CustomerDashboardResponse {
    private Object profile;
    private List<Object> shopkeepers;
    private List<ShopOutstanding> outstandingPerShop;
    private List<Object> recentPurchases;
    private List<Object> pendingPayments;
    private List<Object> notifications;
    private List<Object> invoices;

    @Getter
    @Setter
    @Builder
    public static class ShopOutstanding {
        private Long shopkeeperId;
        private Long customerId;
        private BigDecimal outstandingBalance;
    }
}
