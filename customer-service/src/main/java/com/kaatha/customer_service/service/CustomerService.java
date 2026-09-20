package com.kaatha.customer_service.service;

import com.kaatha.customer_service.dto.request.RegisterCustomerRequest;
import com.kaatha.customer_service.dto.request.SetDefaultShopRequest;
import com.kaatha.customer_service.dto.response.CustomerResponse;
import com.kaatha.customer_service.dto.response.CustomerShopSummaryResponse;
import org.springframework.data.jpa.repository.Query;

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

    @Query("""
       SELECT c
       FROM shopkeeper as s
       JOIN customer as c
       WHERE s.shopkeeper_id = :shopkeeperId
       """)
    List<Customer> searchCustomers(Long shopkeeperId);
}
