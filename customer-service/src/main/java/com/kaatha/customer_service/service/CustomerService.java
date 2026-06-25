package com.kaatha.customer_service.service;

import com.kaatha.customer_service.dto.request.RegisterCustomerRequest;
import com.kaatha.customer_service.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse registerCustomer(RegisterCustomerRequest request);

    CustomerResponse getCustomerById(Long customerId);

    CustomerResponse getCustomerByPhone(String phoneNumber);

    List<CustomerResponse> getCustomersByShopkeeper(Long shopkeeperId);
    boolean existsByPhoneNumber(String phoneNumber);

    Long getDefaultShopkeeper(String phoneNumber);
}