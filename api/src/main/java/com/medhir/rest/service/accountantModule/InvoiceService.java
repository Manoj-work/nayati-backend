package com.medhir.rest.service.accountantModule;

import com.medhir.rest.dto.accountingModule.invoice.InvoiceCreateDTO;
import com.medhir.rest.dto.accountingModule.invoice.InvoiceResponse;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.mapper.accountantModule.InvoiceMapper;
import com.medhir.rest.model.accountantModule.Invoice;
import com.medhir.rest.model.accountantModule.Receipt;
import com.medhir.rest.repository.accountantModule.InvoiceRepository;
import com.medhir.rest.repository.accountantModule.ReceiptRepository;
import com.medhir.rest.testModuleforsales.Customer;
import com.medhir.rest.testModuleforsales.CustomerRepository;
import com.medhir.rest.testModuleforsales.Project;
import com.medhir.rest.testModuleforsales.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final ProjectRepository projectRepository;
    private final CustomerRepository customerRepository;
    private final ReceiptRepository receiptRepository;

    @Transactional
    public Invoice createInvoice(InvoiceCreateDTO dto) {

        // Check for duplicate invoice number
        if (invoiceRepository.existsByInvoiceNumber(dto.getInvoiceNumber())) {
            throw new DuplicateResourceException("Invoice number already exists!");
        }

        // Map DTO to Invoice model (basic fields, items, totals, etc.)
        Invoice invoice = invoiceMapper.toInvoice(dto);

        // Allocate linked receipts if provided
        BigDecimal totalAllocated = BigDecimal.ZERO;

        if (dto.getLinkedReceipts() != null && !dto.getLinkedReceipts().isEmpty()) {

            for (InvoiceCreateDTO.LinkedReceiptDTO linked : dto.getLinkedReceipts()) {

                // Fetch the receipt
                Receipt receipt = receiptRepository.findByReceiptNumber(linked.getReceiptNumber())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Receipt not found: " + linked.getReceiptNumber()
                        ));

                // Calculate how much is unallocated
                BigDecimal receiptAllocated = receipt.getAllocatedAmount() != null ? receipt.getAllocatedAmount() : BigDecimal.ZERO;
                BigDecimal receiptAvailable = receipt.getAmountReceived().subtract(receiptAllocated);

                // Check for over-allocation
                if (linked.getAmountAllocated().compareTo(receiptAvailable) > 0) {
                    throw new IllegalArgumentException(
                            "Cannot allocate " + linked.getAmountAllocated() + " from receipt "
                                    + linked.getReceiptNumber() + ". Available: " + receiptAvailable);
                }

                // Update Receipt's linkedInvoices
                receipt.getLinkedInvoices().add(
                        Receipt.LinkedInvoice.builder()
                                .invoiceNumber(dto.getInvoiceNumber())
                                .amountAllocated(linked.getAmountAllocated())
                                .build()
                );

                // Update Receipt's allocated amount
                receipt.setAllocatedAmount(receiptAllocated.add(linked.getAmountAllocated()));

                receiptRepository.save(receipt);

                // Add LinkedReceipt in Invoice
                invoice.getLinkedReceipts().add(
                        Invoice.LinkedReceipt.builder()
                                .receiptNumber(linked.getReceiptNumber())
                                .amountAllocated(linked.getAmountAllocated())
                                .build()
                );

                totalAllocated = totalAllocated.add(linked.getAmountAllocated());
            }
        }

        // Set Invoice amountReceived, update amountRemaining, update status
        invoice.setAmountReceived(totalAllocated);

        BigDecimal amountRemaining = invoice.getTotalAmount().subtract(totalAllocated);

        if (amountRemaining.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(Invoice.Status.PAID);
        } else if (totalAllocated.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(Invoice.Status.PARTIALLYPAID);
        } else {
            invoice.setStatus(Invoice.Status.PENDING);
        }

        // Save the invoice with all links
        return invoiceRepository.save(invoice);
    }


    public InvoiceResponse getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with number: " + invoiceNumber));

        // Fetch related Project and Customer by their IDs
        Project project = projectRepository.findByProjectId(invoice.getProjectId()).orElse(null);
        Customer customer = customerRepository.findByCustomerId(invoice.getCustomerId()).orElse(null);

        InvoiceResponse.ProjectInfo projectInfo = (project != null)
                ? new InvoiceResponse.ProjectInfo(project.getProjectId(), project.getProjectName(), project.getSiteAddress())
                : null;

        InvoiceResponse.CustomerInfo customerInfo = (customer != null)
                ? new InvoiceResponse.CustomerInfo(customer.getCustomerId(), customer.getCustomerName())
                : null;

        List<InvoiceResponse.InvoiceItem> items = invoice.getItems().stream()
                .map(item -> new InvoiceResponse.InvoiceItem(
                        item.getItemName(),
                        item.getDescription(),
                        item.getHsnOrSac(),
                        item.getQuantity(),
                        item.getUom(),
                        BigDecimal.valueOf(item.getRate()),
                        BigDecimal.valueOf(item.getGstPercentage()),
                        BigDecimal.valueOf(item.getTotal())
                ))
                .collect(Collectors.toList());
        List<InvoiceResponse.LinkedReceiptInfo> linkedReceipts = invoice.getLinkedReceipts() != null
                ? invoice.getLinkedReceipts().stream()
                .map(lr -> new InvoiceResponse.LinkedReceiptInfo(
                        lr.getReceiptNumber(),
                        lr.getAmountAllocated()
                ))
                .collect(Collectors.toList())
                : List.of();

        return new InvoiceResponse(
                invoice.getId(),
                projectInfo,
                customerInfo,
                invoice.getInvoiceNumber(),
                invoice.getInvoiceDate(),
                invoice.getDueDate(),
                invoice.getSubtotal(),
                invoice.getTotalGst(),
                invoice.getTotalAmount(),
                invoice.getAmountReceived(),
                invoice.getAmountRemaining(),
                items,
                invoice.getStatus().name(),
                linkedReceipts
        );
    }

    public List<InvoiceResponse> getAllInvoices() {
        // Get all invoices
        List<Invoice> invoices = invoiceRepository.findAll();

        // Collect unique projectIds and customerIds
        Set<String> projectIds = invoices.stream()
                .map(Invoice::getProjectId)
                .collect(Collectors.toSet());

        Set<String> customerIds = invoices.stream()
                .map(Invoice::getCustomerId)
                .collect(Collectors.toSet());

        // Fetch all related projects and customers in 1 DB call each
        List<Project> projects = projectRepository.findAllByProjectIdIn(projectIds);
        List<Customer> customers = customerRepository.findAllByCustomerIdIn(customerIds);

        // Build lookup maps
        Map<String, Project> projectMap = projects.stream()
                .collect(Collectors.toMap(Project::getProjectId, p -> p));

        Map<String, Customer> customerMap = customers.stream()
                .collect(Collectors.toMap(Customer::getCustomerId, c -> c));

        // Map each invoice to InvoiceResponse
        return invoices.stream().map(invoice -> {

            Project project = projectMap.get(invoice.getProjectId());
            Customer customer = customerMap.get(invoice.getCustomerId());

            InvoiceResponse.ProjectInfo projectInfo = (project != null)
                    ? new InvoiceResponse.ProjectInfo(
                    project.getProjectId(),
                    project.getProjectName(),
                    project.getSiteAddress()
            )
                    : null;

            InvoiceResponse.CustomerInfo customerInfo = (customer != null)
                    ? new InvoiceResponse.CustomerInfo(
                    customer.getCustomerId(),
                    customer.getCustomerName()
            )
                    : null;

            List<InvoiceResponse.InvoiceItem> items = invoice.getItems().stream()
                    .map(item -> new InvoiceResponse.InvoiceItem(
                            item.getItemName(),
                            item.getDescription(),
                            item.getHsnOrSac(),
                            item.getQuantity(),
                            item.getUom(),
                            BigDecimal.valueOf(item.getRate()),
                            BigDecimal.valueOf(item.getGstPercentage()),
                            BigDecimal.valueOf(item.getTotal())
                    ))
                    .collect(Collectors.toList());

            List<InvoiceResponse.LinkedReceiptInfo> linkedReceipts = invoice.getLinkedReceipts() != null
                    ? invoice.getLinkedReceipts().stream()
                    .map(lr -> new InvoiceResponse.LinkedReceiptInfo(
                            lr.getReceiptNumber(),
                            lr.getAmountAllocated()
                    ))
                    .collect(Collectors.toList())
                    : List.of();

            return new InvoiceResponse(
                    invoice.getId(),
                    projectInfo,
                    customerInfo,
                    invoice.getInvoiceNumber(),
                    invoice.getInvoiceDate(),
                    invoice.getDueDate(),
                    invoice.getSubtotal(),
                    invoice.getTotalGst(),
                    invoice.getTotalAmount(),
                    invoice.getAmountReceived(),
                    invoice.getAmountRemaining(),
                    items,
                    invoice.getStatus().name(),
                    linkedReceipts
            );

        }).collect(Collectors.toList());
    }
    public List<InvoiceResponse> getInvoicesByProjectId(String projectId) {
        // Get only invoices for the given projectId
        List<Invoice> invoices = invoiceRepository.findAllByProjectId(projectId);

        if (invoices.isEmpty()) {
            return List.of(); // Return empty list if none found
        }

        // Get unique customerIds from these invoices
        Set<String> customerIds = invoices.stream()
                .map(Invoice::getCustomerId)
                .collect(Collectors.toSet());

        // Fetch related Project once (should be unique)
        Project project = projectRepository.findByProjectId(projectId)
                .orElse(null);

        // Fetch all related customers
        List<Customer> customers = customerRepository.findAllByCustomerIdIn(customerIds);

        Map<String, Customer> customerMap = customers.stream()
                .collect(Collectors.toMap(Customer::getCustomerId, c -> c));

        // Map each invoice to InvoiceResponse
        return invoices.stream().map(invoice -> {

            Customer customer = customerMap.get(invoice.getCustomerId());

            InvoiceResponse.ProjectInfo projectInfo = (project != null)
                    ? new InvoiceResponse.ProjectInfo(
                    project.getProjectId(),
                    project.getProjectName(),
                    project.getSiteAddress()
            )
                    : null;

            InvoiceResponse.CustomerInfo customerInfo = (customer != null)
                    ? new InvoiceResponse.CustomerInfo(
                    customer.getCustomerId(),
                    customer.getCustomerName()
            )
                    : null;

            List<InvoiceResponse.InvoiceItem> items = invoice.getItems().stream()
                    .map(item -> new InvoiceResponse.InvoiceItem(
                            item.getItemName(),
                            item.getDescription(),
                            item.getHsnOrSac(),
                            item.getQuantity(),
                            item.getUom(),
                            BigDecimal.valueOf(item.getRate()),
                            BigDecimal.valueOf(item.getGstPercentage()),
                            BigDecimal.valueOf(item.getTotal())
                    ))
                    .collect(Collectors.toList());
            List<InvoiceResponse.LinkedReceiptInfo> linkedReceipts = invoice.getLinkedReceipts() != null
                    ? invoice.getLinkedReceipts().stream()
                    .map(lr -> new InvoiceResponse.LinkedReceiptInfo(
                            lr.getReceiptNumber(),
                            lr.getAmountAllocated()
                    ))
                    .collect(Collectors.toList())
                    : List.of();

            return new InvoiceResponse(
                    invoice.getId(),
                    projectInfo,
                    customerInfo,
                    invoice.getInvoiceNumber(),
                    invoice.getInvoiceDate(),
                    invoice.getDueDate(),
                    invoice.getSubtotal(),
                    invoice.getTotalGst(),
                    invoice.getTotalAmount(),
                    invoice.getAmountReceived(),
                    invoice.getAmountRemaining(),
                    items,
                    invoice.getStatus().name(),
                    linkedReceipts
            );

        }).collect(Collectors.toList());
    }

}