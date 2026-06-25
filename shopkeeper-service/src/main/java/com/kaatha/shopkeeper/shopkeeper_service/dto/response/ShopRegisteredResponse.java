package com.kaatha.shopkeeper.shopkeeper_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Builder
public class ShopRegisteredResponse {

    private Long id;
    private Long shopkeeperId;
    private String shopName;
    private String businessType;
    private String addressLineOne;
    private String addressLineSecond;
    private String city;
    private String state;
    private String pinCode;
    private Boolean isActive;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
