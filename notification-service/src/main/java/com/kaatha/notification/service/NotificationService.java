package com.kaatha.notification.service;

import com.kaatha.notification.dto.request.NotificationRequest;
import com.kaatha.notification.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse sendNotification(NotificationRequest request);
    List<NotificationResponse> getNotificationsByPhone(String phone);
}
