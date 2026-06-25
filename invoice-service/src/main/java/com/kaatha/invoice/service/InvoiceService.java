package com.kaatha.invoice.service;

import com.kaatha.invoice.dto.request.GenerateInvoiceRequest;
import com.kaatha.invoice.dto.response.InvoiceResponse;

import java.util.List;

public interface InvoiceService {

    InvoiceResponse generateInvoice(GenerateInvoiceRequest request);

    InvoiceResponse getInvoice(Long id);

    InvoiceResponse getByInvoiceNumber(String invoiceNumber);

    List<InvoiceResponse> getByCustomer(Long customerId);

    List<InvoiceResponse> getByShopkeeper(Long shopkeeperId);

    byte[] generatePdf(Long id);
}
