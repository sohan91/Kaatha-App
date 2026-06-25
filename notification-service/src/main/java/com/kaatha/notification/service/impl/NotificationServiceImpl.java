package com.kaatha.notification.service.impl;

import com.kaatha.notification.controller.RealtimeNotificationController;
import com.kaatha.notification.dto.request.NotificationRequest;
import com.kaatha.notification.dto.response.NotificationResponse;
import com.kaatha.notification.entity.Notification;
import com.kaatha.notification.repository.NotificationRepository;
import com.kaatha.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {
        // Log the notification (SMS integration can be added here e.g. Twilio, MSG91)
        log.info("[NOTIFICATION][{}] To: {} | Message: {}",
                request.getType(), request.getRecipientPhone(), request.getMessage());

        Notification notification = Notification.builder()
                .recipientPhone(request.getRecipientPhone())
                .message(request.getMessage())
                .type(request.getType())
                .sentAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);

        RealtimeNotificationController.broadcast(request.getRecipientPhone(),
                NotificationResponse.builder()
                        .id(saved.getId())
                        .recipientPhone(saved.getRecipientPhone())
                        .message(saved.getMessage())
                        .type(saved.getType())
                        .sentAt(saved.getSentAt())
                        .createdAt(saved.getCreatedAt())
                        .build());

        return NotificationResponse.builder()
                .id(saved.getId())
                .recipientPhone(saved.getRecipientPhone())
                .message(saved.getMessage())
                .type(saved.getType())
                .sentAt(saved.getSentAt())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public List<NotificationResponse> getNotificationsByPhone(String phone) {
        return notificationRepository.findByRecipientPhoneOrderByCreatedAtDesc(phone)
                .stream()
                .map(n -> NotificationResponse.builder()
                        .id(n.getId())
                        .recipientPhone(n.getRecipientPhone())
                        .message(n.getMessage())
                        .type(n.getType())
                        .sentAt(n.getSentAt())
                        .createdAt(n.getCreatedAt())
                        .build())
                .toList();
    }
}
