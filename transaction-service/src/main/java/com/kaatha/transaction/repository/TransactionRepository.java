package com.kaatha.transaction.repository;

import com.kaatha.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCustomerIdOrderByTransactionDateDesc(Long customerId);

    List<Transaction> findByShopkeeperIdOrderByTransactionDateDesc(Long shopkeeperId);

    @Query("SELECT COALESCE(SUM(t.amountPaid), 0) FROM Transaction t WHERE t.shopkeeperId = :shopkeeperId " +
           "AND t.type = 'PAYMENT' AND t.transactionDate >= :start AND t.transactionDate < :end")
    BigDecimal sumTodayCollections(@Param("shopkeeperId") Long shopkeeperId,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);
}
