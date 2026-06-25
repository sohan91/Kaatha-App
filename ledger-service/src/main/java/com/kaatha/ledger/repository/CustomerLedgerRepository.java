package com.kaatha.ledger.repository;

import com.kaatha.ledger.entity.CustomerLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerLedgerRepository extends JpaRepository<CustomerLedger, Long> {
    Optional<CustomerLedger> findByShopkeeperIdAndCustomerId(Long shopkeeperId, Long customerId);
    List<CustomerLedger> findByCustomerId(Long customerId);
    List<CustomerLedger> findByShopkeeperId(Long shopkeeperId);
}
