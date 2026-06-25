package com.kaatha.customer_service.repository;

import com.kaatha.customer_service.mapper.ShopCustomerMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopCustomerMappingRepository extends JpaRepository<ShopCustomerMapping, Long> {

    boolean existsByShopkeeperIdAndCustomerId(Long shopkeeperId, Long customerId);

    List<ShopCustomerMapping> findByShopkeeperId(Long shopkeeperId);

    Optional<ShopCustomerMapping> findFirstByCustomerIdOrderByIdAsc(Long customerId);
}