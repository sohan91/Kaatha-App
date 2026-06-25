package com.kaatha.customer_service.service.serviceImpl;

import com.kaatha.customer_service.dto.request.RegisterCustomerRequest;
import com.kaatha.customer_service.dto.response.CustomerResponse;
import com.kaatha.customer_service.entity.Customer;
import com.kaatha.customer_service.exception.CustomerAlreadyExistsException;
import com.kaatha.customer_service.exception.CustomerNotFoundException;
import com.kaatha.customer_service.mapper.CustomerMapper;
import com.kaatha.customer_service.mapper.ShopCustomerMapping;
import com.kaatha.customer_service.repository.CustomerRepository;
import com.kaatha.customer_service.repository.ShopCustomerMappingRepository;
import com.kaatha.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ShopCustomerMappingRepository mappingRepository;
    private final CustomerMapper customerMapper;
    @Override
    public CustomerResponse registerCustomer(RegisterCustomerRequest request) {

        // 1. Check if customer exists by phone
        Customer customer = customerRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElse(null);

        // 2. If customer exists → check mapping
        if (customer != null) {

            boolean mappingExists =
                    mappingRepository.existsByShopkeeperIdAndCustomerId(
                            request.getShopkeeperId(),
                            customer.getId()
                    );

            if (mappingExists) {
                throw new CustomerAlreadyExistsException(
                        "Customer already exists under this shopkeeper with phone: "
                                + request.getPhoneNumber()
                );
            }
        }

        // 3. If customer not exist → create
        if (customer == null) {
            customer = customerRepository.save(
                    customerMapper.toEntity(request)
            );
        }

        // 4. Create mapping
        ShopCustomerMapping mapping = ShopCustomerMapping.builder()
                .shopkeeperId(request.getShopkeeperId())
                .customerId(customer.getId())
                .active(true)
                .build();

        mappingRepository.save(mapping);

        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerById(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with id: " + customerId));

        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerByPhone(String phoneNumber) {

        Customer customer = customerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with phone: " + phoneNumber));

        return customerMapper.toResponse(customer);
    }

    @Override
    public List<CustomerResponse> getCustomersByShopkeeper(Long shopkeeperId) {

        List<Long> customerIds = mappingRepository.findByShopkeeperId(shopkeeperId)
                .stream()
                .map(ShopCustomerMapping::getCustomerId)
                .toList();

        return customerRepository.findByIdIn(customerIds)
                .stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    @Override
    public Long getDefaultShopkeeper(String phoneNumber) {

        Customer customer = customerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with phone: " + phoneNumber));

        return mappingRepository
                .findFirstByCustomerIdOrderByIdAsc(customer.getId())
                .map(mapping -> mapping.getShopkeeperId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "No shopkeeper mapping found for phone: " + phoneNumber));
    }
}