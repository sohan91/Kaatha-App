package com.kaatha.shopkeeper.shopkeeper_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShopkeeperResponse {

    private Long id;

    private String shopName;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String gstNumber;
    private String shopCategory;

    private String accountHolderName;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String razorpayAccountId;

    private Boolean active;
}