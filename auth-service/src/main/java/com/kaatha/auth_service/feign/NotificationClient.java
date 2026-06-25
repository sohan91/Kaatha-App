package com.kaatha.auth_service.feign;

import com.kaatha.auth_service.dto.request.NotificationRequest;
import com.kaatha.auth_service.dto.response.ApiResponse;
import com.kaatha.auth_service.dto.response.NotificationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/notifications/send")
    ApiResponse<NotificationResponse> sendNotification(@RequestBody NotificationRequest request);
}
