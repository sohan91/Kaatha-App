package com.kaatha.transaction.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateRequest {
    private Long itemId;
    private Integer quantityChange;
}
