package com.kaatha.payment.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopkeeperResponse {
    private Long id;
    private String shopName;
    private String phoneNumber;
    private String razorpayAccountId;
}
