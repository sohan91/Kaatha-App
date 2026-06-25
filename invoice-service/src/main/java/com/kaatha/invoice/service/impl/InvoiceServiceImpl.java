package com.kaatha.invoice.service.impl;

import com.kaatha.invoice.dto.request.GenerateInvoiceRequest;
import com.kaatha.invoice.dto.response.InvoiceResponse;
import com.kaatha.invoice.entity.Invoice;
import com.kaatha.invoice.repository.InvoiceRepository;
import com.kaatha.invoice.service.InvoiceService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Override
    public InvoiceResponse generateInvoice(GenerateInvoiceRequest request) {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String prefix = "INV-" + request.getShopkeeperId() + "-" + datePart + "-";
        long sequence = invoiceRepository.countByShopkeeperIdAndInvoiceNumberStartingWith(
                request.getShopkeeperId(), prefix) + 1;
        String invoiceNumber = prefix + String.format("%04d", sequence);

        Invoice invoice = invoiceRepository.save(Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .shopkeeperId(request.getShopkeeperId())
                .customerId(request.getCustomerId())
                .transactionId(request.getTransactionId())
                .shopName(request.getShopName())
                .shopAddress(request.getShopAddress())
                .shopPhone(request.getShopPhone())
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .itemsJson(request.getItemsJson())
                .subtotal(request.getSubtotal())
                .tax(defaultZero(request.getTax()))
                .discount(defaultZero(request.getDiscount()))
                .finalAmount(request.getFinalAmount())
                .amountPaid(defaultZero(request.getAmountPaid()))
                .outstandingAmount(defaultZero(request.getOutstandingAmount()))
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentStatus())
                .build());

        return toResponse(invoice);
    }

    @Override
    public InvoiceResponse getInvoice(Long id) {
        return toResponse(invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found")));
    }

    @Override
    public InvoiceResponse getByInvoiceNumber(String invoiceNumber) {
        return toResponse(invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found")));
    }

    @Override
    public List<InvoiceResponse> getByCustomer(Long customerId) {
        return invoiceRepository.findByCustomerIdOrderByInvoiceDateDesc(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<InvoiceResponse> getByShopkeeper(Long shopkeeperId) {
        return invoiceRepository.findByShopkeeperIdOrderByInvoiceDateDesc(shopkeeperId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public byte[] generatePdf(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            document.add(new Paragraph("INVOICE", titleFont));
            document.add(new Paragraph("Invoice No: " + invoice.getInvoiceNumber(), normalFont));
            document.add(new Paragraph("Date: " + invoice.getInvoiceDate(), normalFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Shop: " + invoice.getShopName(), normalFont));
            document.add(new Paragraph("Address: " + nullSafe(invoice.getShopAddress()), normalFont));
            document.add(new Paragraph("Phone: " + nullSafe(invoice.getShopPhone()), normalFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Customer: " + invoice.getCustomerName(), normalFont));
            document.add(new Paragraph("Phone: " + invoice.getCustomerPhone(), normalFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Items: " + invoice.getItemsJson(), normalFont));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(2);
            addRow(table, "Subtotal", invoice.getSubtotal(), normalFont);
            addRow(table, "Tax", invoice.getTax(), normalFont);
            addRow(table, "Discount", invoice.getDiscount(), normalFont);
            addRow(table, "Final Amount", invoice.getFinalAmount(), normalFont);
            addRow(table, "Amount Paid", invoice.getAmountPaid(), normalFont);
            addRow(table, "Outstanding", invoice.getOutstandingAmount(), normalFont);
            document.add(table);

            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Payment Method: " + nullSafe(invoice.getPaymentMethod()), normalFont));
            document.add(new Paragraph("Status: " + nullSafe(invoice.getPaymentStatus()), normalFont));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private void addRow(PdfPTable table, String label, BigDecimal value, Font font) {
        table.addCell(new PdfPCell(new Phrase(label, font)));
        table.addCell(new PdfPCell(new Phrase(value != null ? value.toPlainString() : "0.00", font)));
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .shopkeeperId(invoice.getShopkeeperId())
                .customerId(invoice.getCustomerId())
                .transactionId(invoice.getTransactionId())
                .shopName(invoice.getShopName())
                .shopAddress(invoice.getShopAddress())
                .shopPhone(invoice.getShopPhone())
                .customerName(invoice.getCustomerName())
                .customerPhone(invoice.getCustomerPhone())
                .itemsJson(invoice.getItemsJson())
                .subtotal(invoice.getSubtotal())
                .tax(invoice.getTax())
                .discount(invoice.getDiscount())
                .finalAmount(invoice.getFinalAmount())
                .amountPaid(invoice.getAmountPaid())
                .outstandingAmount(invoice.getOutstandingAmount())
                .paymentMethod(invoice.getPaymentMethod())
                .paymentStatus(invoice.getPaymentStatus())
                .invoiceDate(invoice.getInvoiceDate())
                .build();
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}
