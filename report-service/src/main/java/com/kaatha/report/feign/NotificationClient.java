package com.kaatha.report.feign;

import com.kaatha.report.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @GetMapping("/notifications/phone/{phone}")
    ApiResponse<List<Map<String, Object>>> getByPhone(@PathVariable String phone);
}
