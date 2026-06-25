package com.kaatha.notification.controller;

import com.kaatha.notification.dto.request.NotificationRequest;
import com.kaatha.notification.dto.response.ApiResponse;
import com.kaatha.notification.dto.response.NotificationResponse;
import com.kaatha.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendNotification(
            @Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<NotificationResponse>builder()
                        .success(true)
                        .message("Notification sent successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByPhone(
            @PathVariable String phone) {
        List<NotificationResponse> response = notificationService.getNotificationsByPhone(phone);
        return ResponseEntity.ok(
                ApiResponse.<List<NotificationResponse>>builder()
                        .success(true)
                        .message("Notifications fetched successfully")
                        .data(response)
                        .build()
        );
    }
}
