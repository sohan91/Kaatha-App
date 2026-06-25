package com.kaatha.invoice.repository;

import com.kaatha.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByCustomerIdOrderByInvoiceDateDesc(Long customerId);

    List<Invoice> findByShopkeeperIdOrderByInvoiceDateDesc(Long shopkeeperId);

    long countByShopkeeperIdAndInvoiceNumberStartingWith(Long shopkeeperId, String prefix);
}
