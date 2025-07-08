package com.medhir.rest.service.accountantModule;

import com.medhir.rest.dto.accountingModule.receipt.ReceiptCreateDTO;
import com.medhir.rest.dto.accountingModule.receipt.ReceiptResponse;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.mapper.accountantModule.ReceiptMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final InvoiceRepository invoiceRepository;
    private final ProjectRepository projectRepository;
    private final ReceiptMapper receiptMapper;
    private final CustomerRepository customerRepository;

    @Transactional
    public Receipt createReceipt(ReceiptCreateDTO dto) {

        if (receiptRepository.findByReceiptNumber(dto.getReceiptNumber()).isPresent()) {
            throw new DuplicateResourceException("Receipt number already exists: " + dto.getReceiptNumber());
        }

        Project project = projectRepository.findByProjectId(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + dto.getProjectId()));

        Receipt receipt = receiptMapper.toReceipt(dto);

        BigDecimal totalAllocated = BigDecimal.ZERO;
        List<Receipt.LinkedInvoice> linkedInvoices = new ArrayList<>();

        if (dto.getLinkedInvoices() != null && !dto.getLinkedInvoices().isEmpty()) {
            for (ReceiptCreateDTO.LinkedInvoiceDTO linked : dto.getLinkedInvoices()) {

                // Find the invoice by number
                Invoice invoice = invoiceRepository.findByInvoiceNumber(linked.getInvoiceNumber())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Invoice not found: " + linked.getInvoiceNumber()));

                if (!invoice.getProjectId().equals(dto.getProjectId())) {
                    throw new IllegalArgumentException(
                            "Invoice " + invoice.getInvoiceNumber() +
                                    " does not belong to project " + dto.getProjectId());
                }

                // Update invoice's amount received
                BigDecimal existingReceived = invoice.getAmountReceived() != null
                        ? invoice.getAmountReceived() : BigDecimal.ZERO;
                BigDecimal updatedReceived = existingReceived.add(linked.getAmountAllocated());
                invoice.setAmountReceived(updatedReceived);

                // Update invoice status
                BigDecimal amountRemaining = invoice.getTotalAmount().subtract(updatedReceived);
                if (amountRemaining.compareTo(BigDecimal.ZERO) <= 0) {
                    invoice.setStatus(Invoice.Status.PAID);
                } else if (updatedReceived.compareTo(BigDecimal.ZERO) > 0) {
                    invoice.setStatus(Invoice.Status.PARTIALLYPAID);
                } else {
                    invoice.setStatus(Invoice.Status.PENDING);
                }

                // Add linked receipt info in the invoice
                invoice.getLinkedReceipts().add(
                        Invoice.LinkedReceipt.builder()
                                .receiptNumber(receipt.getReceiptNumber())
                                .amountAllocated(linked.getAmountAllocated())
                                .build()
                );

                invoiceRepository.save(invoice);

                // Add linked invoice info to receipt
                linkedInvoices.add(
                        Receipt.LinkedInvoice.builder()
                                .invoiceNumber(invoice.getInvoiceNumber())
                                .amountAllocated(linked.getAmountAllocated())
                                .build()
                );

                totalAllocated = totalAllocated.add(linked.getAmountAllocated());
            }
        }

        if (totalAllocated.compareTo(dto.getAmountReceived()) > 0) {
            throw new RuntimeException("Allocated amount exceeds total received amount!");
        }

        receipt.setLinkedInvoices(linkedInvoices);
        receipt.setAllocatedAmount(totalAllocated);

        return receiptRepository.save(receipt);
    }



    public ReceiptResponse getReceiptByNumber(String receiptNumber) {

        Receipt receipt = receiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receipt not found with number: " + receiptNumber
                ));

        Project project = projectRepository.findByProjectId(receipt.getProjectId()).orElse(null);
        Customer customer = customerRepository.findByCustomerId(receipt.getCustomerId()).orElse(null);

        ReceiptResponse.ProjectInfo projectInfo = (project != null)
                ? new ReceiptResponse.ProjectInfo(project.getProjectId(), project.getProjectName(), project.getSiteAddress())
                : null;

        ReceiptResponse.CustomerInfo customerInfo = (customer != null)
                ? new ReceiptResponse.CustomerInfo(customer.getCustomerId(), customer.getCustomerName())
                : null;

        List<ReceiptResponse.LinkedInvoice> linkedInvoices = receipt.getLinkedInvoices().stream()
                .map(linked -> new ReceiptResponse.LinkedInvoice(
                        linked.getInvoiceNumber(),
                        linked.getAmountAllocated()
                ))
                .collect(Collectors.toList());

        return new ReceiptResponse(
                receipt.getId(),
                projectInfo,
                customerInfo,
                receipt.getReceiptNumber(),
                receipt.getReceiptDate(),
                receipt.getAmountReceived(),
                receipt.getAllocatedAmount(),
                receipt.getAmountReceived().subtract(receipt.getAllocatedAmount()),
                linkedInvoices
        );
    }

    public List<ReceiptResponse> getAllReceipts() {

        List<Receipt> receipts = receiptRepository.findAll();

        Set<String> projectIds = receipts.stream()
                .map(Receipt::getProjectId)
                .collect(Collectors.toSet());

        Set<String> customerIds = receipts.stream()
                .map(Receipt::getCustomerId)
                .collect(Collectors.toSet());

        List<Project> projects = projectRepository.findAllByProjectIdIn(projectIds);
        List<Customer> customers = customerRepository.findAllByCustomerIdIn(customerIds);

        Map<String, Project> projectMap = projects.stream()
                .collect(Collectors.toMap(Project::getProjectId, p -> p));

        Map<String, Customer> customerMap = customers.stream()
                .collect(Collectors.toMap(Customer::getCustomerId, c -> c));

        return receipts.stream().map(receipt -> {

            Project project = projectMap.get(receipt.getProjectId());
            Customer customer = customerMap.get(receipt.getCustomerId());

            ReceiptResponse.ProjectInfo projectInfo = (project != null)
                    ? new ReceiptResponse.ProjectInfo(project.getProjectId(), project.getProjectName(), project.getSiteAddress())
                    : null;

            ReceiptResponse.CustomerInfo customerInfo = (customer != null)
                    ? new ReceiptResponse.CustomerInfo(customer.getCustomerId(), customer.getCustomerName())
                    : null;

            List<ReceiptResponse.LinkedInvoice> linkedInvoices = receipt.getLinkedInvoices().stream()
                    .map(linked -> new ReceiptResponse.LinkedInvoice(
                            linked.getInvoiceNumber(),
                            linked.getAmountAllocated()
                    ))
                    .collect(Collectors.toList());

            return new ReceiptResponse(
                    receipt.getId(),
                    projectInfo,
                    customerInfo,
                    receipt.getReceiptNumber(),
                    receipt.getReceiptDate(),
                    receipt.getAmountReceived(),
                    receipt.getAllocatedAmount(),
                    receipt.getAmountReceived().subtract(receipt.getAllocatedAmount()),
                    linkedInvoices
            );

        }).collect(Collectors.toList());
    }

    public List<ReceiptResponse> getReceiptsByProjectId(String projectId) {

        List<Receipt> receipts = receiptRepository.findAllByProjectId(projectId);

        if (receipts.isEmpty()) {
            return List.of();
        }

        Set<String> customerIds = receipts.stream()
                .map(Receipt::getCustomerId)
                .collect(Collectors.toSet());

        Project project = projectRepository.findByProjectId(projectId).orElse(null);

        List<Customer> customers = customerRepository.findAllByCustomerIdIn(customerIds);

        Map<String, Customer> customerMap = customers.stream()
                .collect(Collectors.toMap(Customer::getCustomerId, c -> c));

        return receipts.stream().map(receipt -> {

            Customer customer = customerMap.get(receipt.getCustomerId());

            ReceiptResponse.ProjectInfo projectInfo = (project != null)
                    ? new ReceiptResponse.ProjectInfo(project.getProjectId(), project.getProjectName(), project.getSiteAddress())
                    : null;

            ReceiptResponse.CustomerInfo customerInfo = (customer != null)
                    ? new ReceiptResponse.CustomerInfo(customer.getCustomerId(), customer.getCustomerName())
                    : null;

            List<ReceiptResponse.LinkedInvoice> linkedInvoices = receipt.getLinkedInvoices().stream()
                    .map(linked -> new ReceiptResponse.LinkedInvoice(
                            linked.getInvoiceNumber(),
                            linked.getAmountAllocated()
                    ))
                    .collect(Collectors.toList());

            return new ReceiptResponse(
                    receipt.getId(),
                    projectInfo,
                    customerInfo,
                    receipt.getReceiptNumber(),
                    receipt.getReceiptDate(),
                    receipt.getAmountReceived(),
                    receipt.getAllocatedAmount(),
                    receipt.getAmountReceived().subtract(receipt.getAllocatedAmount()),
                    linkedInvoices
            );

        }).collect(Collectors.toList());
    }

    public List<ReceiptResponse> getUnallocatedReceipts(String projectId) {
        List<Receipt> receipts = receiptRepository.findAllByProjectId(projectId);

        return receipts.stream()
                .filter(r -> r.getAmountReceived().subtract(
                        r.getAllocatedAmount() != null ? r.getAllocatedAmount() : BigDecimal.ZERO
                ).compareTo(BigDecimal.ZERO) > 0)
                .map(r -> {
                    BigDecimal allocated = r.getAllocatedAmount() != null ? r.getAllocatedAmount() : BigDecimal.ZERO;
                    return new ReceiptResponse(
                            r.getId(),
                            null,  // or ProjectInfo if you want
                            null,  // or CustomerInfo if you want
                            r.getReceiptNumber(),
                            r.getReceiptDate(),
                            r.getAmountReceived(),
                            allocated,
                            r.getAmountReceived().subtract(allocated),
                            List.of() // skip linkedInvoices here
                    );
                }).collect(Collectors.toList());
    }


}
