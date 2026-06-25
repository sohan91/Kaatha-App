package com.kaatha.common.events;

import com.kaatha.common.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KaathaEvent {
    private String eventId;
    private EventType eventType;
    private Long shopkeeperId;
    private Long customerId;
    private Long transactionId;
    private String recipientPhone;
    private Map<String, Object> payload;
    private LocalDateTime timestamp;
}
