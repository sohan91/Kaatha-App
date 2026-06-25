package com.kaatha.item.repository;

import com.kaatha.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByShopkeeperIdAndActiveTrue(Long shopkeeperId);
    List<Item> findByIdIn(List<Long> ids);
}
