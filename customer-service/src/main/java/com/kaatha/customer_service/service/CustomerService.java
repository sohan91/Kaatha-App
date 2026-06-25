package com.kaatha.customer_service.service;

import com.kaatha.customer_service.dto.request.RegisterCustomerRequest;
import com.kaatha.customer_service.dto.request.SetDefaultShopRequest;
import com.kaatha.customer_service.dto.response.CustomerResponse;
import com.kaatha.customer_service.dto.response.CustomerShopSummaryResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse registerCustomer(RegisterCustomerRequest request);

    CustomerResponse getCustomerById(Long customerId);

    CustomerResponse getCustomerByShopkeeperAndPhone(Long shopkeeperId, String phoneNumber);

    List<CustomerResponse> getCustomersByShopkeeper(Long shopkeeperId);

    List<CustomerShopSummaryResponse> getShopkeepersForPhone(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByShopkeeperAndPhone(Long shopkeeperId, String phoneNumber);

    Long getDefaultShopkeeper(String phoneNumber);

    void setDefaultShopkeeper(SetDefaultShopRequest request);

    List<CustomerResponse> searchCustomers(Long shopkeeperId, String query);
}
