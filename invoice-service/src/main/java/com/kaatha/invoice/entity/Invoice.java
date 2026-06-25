package com.kaatha.invoice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    private Long shopkeeperId;
    private Long customerId;
    private Long transactionId;

    private String shopName;
    private String shopAddress;
    private String shopPhone;
    private String customerName;
    private String customerPhone;

    @Column(columnDefinition = "TEXT")
    private String itemsJson;

    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal finalAmount;
    private BigDecimal amountPaid;
    private BigDecimal outstandingAmount;

    private String paymentMethod;
    private String paymentStatus;

    @CreationTimestamp
    private LocalDateTime invoiceDate;
}
