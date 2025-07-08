package com.medhir.rest.controller.accountantModule;

import com.medhir.rest.dto.accountingModule.invoice.InvoiceCreateDTO;
import com.medhir.rest.dto.accountingModule.invoice.InvoiceResponse;
import com.medhir.rest.model.accountantModule.Invoice;
import com.medhir.rest.service.accountantModule.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<?> createInvoice(@Valid @RequestBody InvoiceCreateDTO request) {
        Invoice createdInvoice = invoiceService.createInvoice(request);

        return ResponseEntity.ok(Map.of(
                "message", "Invoice created successfully!"
//                "invoice", createdInvoice
        ));
    }

    @GetMapping
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{invoiceNumber}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable String invoiceNumber) {
        InvoiceResponse invoiceResponse = invoiceService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(invoiceResponse);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByProjectId(@PathVariable String projectId){
        List<InvoiceResponse> invoices  = invoiceService.getInvoicesByProjectId(projectId);
        return ResponseEntity.ok(invoices);
    }

}
