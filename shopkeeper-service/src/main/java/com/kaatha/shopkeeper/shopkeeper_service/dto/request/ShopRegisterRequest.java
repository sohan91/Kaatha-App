package com.kaatha.shopkeeper.shopkeeper_service.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopRegisterRequest {


    private Long shopkeeperId;


    private String shopName;

    private String businessType;


    private String addressLineOne;

    private String addressLineSecond;

    private String city;

    private String state;

    private String pinCode;
}
