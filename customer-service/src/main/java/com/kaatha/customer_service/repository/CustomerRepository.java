package com.kaatha.customer_service.repository;

import com.kaatha.customer_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    List<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByShopkeeperIdAndPhoneNumber(Long shopkeeperId, String phoneNumber);

    boolean existsByShopkeeperIdAndPhoneNumber(Long shopkeeperId, String phoneNumber);

    List<Customer> findByShopkeeperId(Long shopkeeperId);

    @Query("SELECT c FROM Customer c WHERE c.shopkeeperId = :shopkeeperId AND c.active = true " +
           "AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR c.phoneNumber LIKE CONCAT('%', :query, '%'))")
    List<Customer> searchByShopkeeper(@Param("shopkeeperId") Long shopkeeperId, @Param("query") String query);
}
