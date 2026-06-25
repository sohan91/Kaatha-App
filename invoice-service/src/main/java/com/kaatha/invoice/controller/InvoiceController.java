package com.kaatha.invoice.controller;

import com.kaatha.invoice.dto.request.GenerateInvoiceRequest;
import com.kaatha.invoice.dto.response.ApiResponse;
import com.kaatha.invoice.dto.response.InvoiceResponse;
import com.kaatha.invoice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<InvoiceResponse>> generate(
            @Valid @RequestBody GenerateInvoiceRequest request) {

        return ResponseEntity.ok(ApiResponse.<InvoiceResponse>builder()
                .success(true)
                .message("Invoice generated")
                .data(invoiceService.generateInvoice(request))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<InvoiceResponse>builder()
                .success(true)
                .message("Invoice fetched")
                .data(invoiceService.getInvoice(id))
                .build());
    }

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getByNumber(
            @PathVariable String invoiceNumber) {

        return ResponseEntity.ok(ApiResponse.<InvoiceResponse>builder()
                .success(true)
                .message("Invoice fetched")
                .data(invoiceService.getByInvoiceNumber(invoiceNumber))
                .build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getByCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(ApiResponse.<List<InvoiceResponse>>builder()
                .success(true)
                .message("Invoices fetched")
                .data(invoiceService.getByCustomer(customerId))
                .build());
    }

    @GetMapping("/shopkeeper/{shopkeeperId}")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getByShopkeeper(
            @PathVariable Long shopkeeperId) {

        return ResponseEntity.ok(ApiResponse.<List<InvoiceResponse>>builder()
                .success(true)
                .message("Invoices fetched")
                .data(invoiceService.getByShopkeeper(shopkeeperId))
                .build());
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdf = invoiceService.generatePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
