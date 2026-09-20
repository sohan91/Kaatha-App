package com.kaatha.shopkeeper.shopkeeper_service.repository;

import com.kaatha.shopkeeper.shopkeeper_service.entity.Shopkeeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopkeeperRepository
        extends JpaRepository<Shopkeeper, Long> {

    boolean existsByPhoneNumber(String phoneNumber);
    Optional<Shopkeeper> findByPhoneNumber(String phoneNumber);

}