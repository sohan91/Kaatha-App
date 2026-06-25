package com.kaatha.ledger.entity;

import com.kaatha.ledger.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_ledgers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"shopkeeper_id", "customer_id"})
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLedger extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shopkeeper_id", nullable = false)
    private Long shopkeeperId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "outstanding_balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @Column(name = "last_transaction_date")
    private LocalDateTime lastTransactionDate;

    @Column(name = "last_transaction_amount", precision = 12, scale = 2)
    private BigDecimal lastTransactionAmount;

    @Column(name = "last_transaction_type")
    private String lastTransactionType;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
