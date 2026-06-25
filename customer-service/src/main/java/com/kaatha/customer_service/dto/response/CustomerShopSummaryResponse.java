package com.kaatha.customer_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CustomerShopSummaryResponse {

    private Long customerId;
    private Long shopkeeperId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
