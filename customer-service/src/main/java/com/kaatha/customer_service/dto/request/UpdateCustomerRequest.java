package com.kaatha.customer_service.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCustomerRequest {

    private String fullName;

    private String email;

    private String address;

    private Boolean active;
}