package com.kaatha.transaction.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String recipientPhone;
    private String message;
    private String type; // OTP, REMINDER, PAYMENT
}
