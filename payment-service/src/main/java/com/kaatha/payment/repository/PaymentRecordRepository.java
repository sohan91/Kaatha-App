package com.kaatha.payment.repository;

import com.kaatha.payment.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    List<PaymentRecord> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<PaymentRecord> findByRazorpayOrderId(String razorpayOrderId);
}
