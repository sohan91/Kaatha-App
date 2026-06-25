package com.kaatha.shopkeeper.shopkeeper_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateShopkeeperRequest {

    private String shopName;
    private String firstName;
    private String lastName;
    private String email;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
}