package com.kaatha.customer_service.mapper;

import com.kaatha.customer_service.dto.request.RegisterCustomerRequest;
import com.kaatha.customer_service.dto.response.CustomerResponse;
import com.kaatha.customer_service.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    // Request → Entity
    public Customer toEntity(RegisterCustomerRequest request) {

        return Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .address(request.getAddress())
                .active(true)
                .deleted(false)
                .build();
    }
    // Entity → Response
    public CustomerResponse toResponse(Customer customer) {

        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .active(customer.getActive())
                .deleted(customer.getDeleted())
                .build();
    }
}