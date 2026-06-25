package com.kaatha.auth_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificationResponse {
    private Long id;
    private String recipientPhone;
    private String message;
    private String type;
    private LocalDateTime sentAt;
}
