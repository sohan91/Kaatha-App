package com.kaatha.customer_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSearchRequest {

    private String phoneNumber;

    private String fullName;

    private Long shopkeeperId;
}