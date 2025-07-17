package com.medhir.rest.controller.accountantModule;

import com.medhir.rest.dto.accountantModule.invoice.InvoiceCreateDTO;
import com.medhir.rest.dto.accountantModule.invoice.InvoiceResponse;
import com.medhir.rest.model.accountantModule.Invoice;
import com.medhir.rest.sales.dto.lead.LeadProjectCustomerResponseDTO;
import com.medhir.rest.sales.repository.LeadRepository;
import com.medhir.rest.sales.service.LeadService;
import com.medhir.rest.service.accountantModule.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    public LeadService leadService;

    @Autowired
    public LeadRepository leadRepository;
    @PostMapping
    public ResponseEntity<?> createInvoice(@Valid @RequestBody InvoiceCreateDTO request) {
        Invoice createdInvoice = invoiceService.createInvoice(request);

        return ResponseEntity.ok(Map.of(
                "message", "Invoice created successfully!",
                "invoice", createdInvoice
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

//    @GetMapping("/")
//    public ResponseEntity<List<InvoiceResponse>> getAllLeads(){
//        List<InvoiceResponse> invoices  = leadService.getAllLeads();
//        return ResponseEntity.ok(invoices);
//    }

    @GetMapping("/invoice-leads")
    public ResponseEntity<List<LeadProjectCustomerResponseDTO>> getInvoiceLeads() {
        List<LeadProjectCustomerResponseDTO> leads = leadService.getLeadsForInvoice();
        return ResponseEntity.ok(leads);
    }



}
