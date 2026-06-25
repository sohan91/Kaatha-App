package com.kaatha.notification.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String recipientPhone;
    private String message;
    private String type;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
