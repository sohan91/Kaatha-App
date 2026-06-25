package com.kaatha.auth_service.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationRequest {
    private String recipientPhone;
    private String message;
    private String type;
}
