package com.kaatha.customer_service.service.serviceImpl;

import com.kaatha.customer_service.dto.request.RegisterCustomerRequest;
import com.kaatha.customer_service.dto.request.SetDefaultShopRequest;
import com.kaatha.customer_service.dto.response.CustomerResponse;
import com.kaatha.customer_service.dto.response.CustomerShopSummaryResponse;
import com.kaatha.customer_service.entity.Customer;
import com.kaatha.customer_service.entity.CustomerPreference;
import com.kaatha.customer_service.exception.CustomerAlreadyExistsException;
import com.kaatha.customer_service.exception.CustomerNotFoundException;
import com.kaatha.customer_service.feign.ShopkeeperClient;
import com.kaatha.customer_service.mapper.CustomerMapper;
import com.kaatha.customer_service.repository.CustomerPreferenceRepository;
import com.kaatha.customer_service.repository.CustomerRepository;
import com.kaatha.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerPreferenceRepository preferenceRepository;
    private final CustomerMapper customerMapper;
    private final ShopkeeperClient shopkeeperClient;

    @Override
    public CustomerResponse registerCustomer(RegisterCustomerRequest request) {
        if (customerRepository.existsByShopkeeperIdAndPhoneNumber(
                request.getShopkeeperId(), request.getPhoneNumber())) {
            throw new CustomerAlreadyExistsException(
                    "Customer already exists under this shopkeeper with phone: "
                            + request.getPhoneNumber());
        }

        Customer customer = customerRepository.save(customerMapper.toEntity(request));

        preferenceRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseGet(() -> preferenceRepository.save(
                        CustomerPreference.builder()
                                .phoneNumber(request.getPhoneNumber())
                                .defaultShopkeeperId(request.getShopkeeperId())
                                .build()));

        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with id: " + customerId));
        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerByShopkeeperAndPhone(Long shopkeeperId, String phoneNumber) {
        Customer customer = customerRepository.findByShopkeeperIdAndPhoneNumber(shopkeeperId, phoneNumber)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found for shopkeeper " + shopkeeperId + " with phone: " + phoneNumber));
        return customerMapper.toResponse(customer);
    }

    @Override
    public List<CustomerResponse> getCustomersByShopkeeper(Long shopkeeperId) {
        return customerRepository.findByShopkeeperId(shopkeeperId).stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Override
    public List<CustomerShopSummaryResponse> getShopkeepersForPhone(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber).stream()
                .map(c -> CustomerShopSummaryResponse.builder()
                        .customerId(c.getId())
                        .shopkeeperId(c.getShopkeeperId())
                        .firstName(c.getFirstName())
                        .lastName(c.getLastName())
                        .phoneNumber(c.getPhoneNumber())
                        .build())
                .toList();
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return customerRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existsByShopkeeperAndPhone(Long shopkeeperId, String phoneNumber) {
        return customerRepository.existsByShopkeeperIdAndPhoneNumber(shopkeeperId, phoneNumber);
    }

    @Override
    public Long getDefaultShopkeeper(String phoneNumber) {
        return preferenceRepository.findByPhoneNumber(phoneNumber)
                .map(CustomerPreference::getDefaultShopkeeperId)
                .orElseGet(() -> customerRepository.findByPhoneNumber(phoneNumber).stream()
                        .findFirst()
                        .map(Customer::getShopkeeperId)
                        .orElseThrow(() -> new CustomerNotFoundException(
                                "No shopkeeper association found for phone: " + phoneNumber)));
    }

    @Override
    public void setDefaultShopkeeper(SetDefaultShopRequest request) {
        boolean associated = customerRepository.existsByShopkeeperIdAndPhoneNumber(
                request.getShopkeeperId(), request.getPhoneNumber());
        if (!associated) {
            throw new CustomerNotFoundException(
                    "Customer is not associated with shopkeeper: " + request.getShopkeeperId());
        }

        CustomerPreference preference = preferenceRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElse(CustomerPreference.builder()
                        .phoneNumber(request.getPhoneNumber())
                        .build());
        preference.setDefaultShopkeeperId(request.getShopkeeperId());
        preferenceRepository.save(preference);
    }

    @Override
    public List<Customer> searchCustomers(Long shopkeeperId) {
        return customerRepository.searchByShopkeeper(shopkeeperClient.findIdShopKeeper(shopkeeperId));
    }
}
