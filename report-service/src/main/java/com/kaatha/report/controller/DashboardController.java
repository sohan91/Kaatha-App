package com.kaatha.report.controller;

import com.kaatha.report.dto.response.ApiResponse;
import com.kaatha.report.dto.response.CustomerDashboardResponse;
import com.kaatha.report.dto.response.ShopkeeperDashboardResponse;
import com.kaatha.report.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/shopkeeper/{shopkeeperId}/dashboard")
    public ResponseEntity<ApiResponse<ShopkeeperDashboardResponse>> shopkeeperDashboard(
            @PathVariable Long shopkeeperId) {

        return ResponseEntity.ok(ApiResponse.<ShopkeeperDashboardResponse>builder()
                .success(true)
                .message("Dashboard fetched")
                .data(dashboardService.getShopkeeperDashboard(shopkeeperId))
                .build());
    }

    @GetMapping("/customer/{phoneNumber}/dashboard")
    public ResponseEntity<ApiResponse<CustomerDashboardResponse>> customerDashboard(
            @PathVariable String phoneNumber) {

        return ResponseEntity.ok(ApiResponse.<CustomerDashboardResponse>builder()
                .success(true)
                .message("Dashboard fetched")
                .data(dashboardService.getCustomerDashboard(phoneNumber))
                .build());
    }
}
