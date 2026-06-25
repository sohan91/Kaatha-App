package com.kaatha.shopkeeper.shopkeeper_service.repository;

import com.kaatha.shopkeeper.shopkeeper_service.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop,Long> {

}
