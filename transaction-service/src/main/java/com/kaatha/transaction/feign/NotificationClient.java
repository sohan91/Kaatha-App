package com.kaatha.transaction.feign;

import com.kaatha.transaction.dto.request.NotificationRequest;
import com.kaatha.transaction.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/notifications/send")
    ApiResponse<?> sendNotification(@RequestBody NotificationRequest request);
}
