package com.kaatha.customer_service.dto.response;

import com.kaatha.customer_service.dto.response.CustomerResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CustomerWithShopkeepersResponse {

    private CustomerResponse customer;

    private List<Long> shopkeeperIds;
}