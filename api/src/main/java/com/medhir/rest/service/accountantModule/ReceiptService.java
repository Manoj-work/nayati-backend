package com.medhir.rest.service.accountantModule;

import com.medhir.rest.dto.accountantModule.receipt.ReceiptCreateDTO;
import com.medhir.rest.dto.accountantModule.receipt.ReceiptResponse;
import com.medhir.rest.dto.accountantModule.receipt.UnallocatedReceiptsResponse;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.mapper.accountantModule.ReceiptMapper;
import com.medhir.rest.model.accountantModule.Invoice;
import com.medhir.rest.model.accountantModule.Receipt;
import com.medhir.rest.repository.accountantModule.InvoiceRepository;
import com.medhir.rest.repository.accountantModule.ReceiptRepository;
import com.medhir.rest.sales.model.LeadModel;
import com.medhir.rest.sales.repository.LeadRepository;
import com.medhir.rest.testModuleforsales.Customer;
import com.medhir.rest.testModuleforsales.CustomerRepository;
import com.medhir.rest.testModuleforsales.Project;
import com.medhir.rest.testModuleforsales.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LeadRepository leadRepository;

    @Transactional
    public Receipt createReceipt(ReceiptCreateDTO dto) {

        if (receiptRepository.findByReceiptNumber(dto.getReceiptNumber()).isPresent()) {
            throw new DuplicateResourceException("Receipt number already exists: " + dto.getReceiptNumber());
        }


        LeadModel project = leadRepository.findByLeadId(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with  ID: " + dto.getProjectId()));


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

        // Since you're using leadId as projectId
        LeadModel lead = leadRepository.findByLeadId(receipt.getProjectId()).orElse(null);
        Customer customer = customerRepository.findByCustomerId(receipt.getCustomerId()).orElse(null);

        ReceiptResponse.ProjectInfo projectInfo = (lead != null)
                ? new ReceiptResponse.ProjectInfo(
                lead.getLeadId(),
                lead.getProjectName(),
                lead.getAddress()
        )
                : null;

        ReceiptResponse.CustomerInfo customerInfo = (customer != null)
                ? new ReceiptResponse.CustomerInfo(
                customer.getCustomerId(),
                customer.getCustomerName()
        )
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
                receipt.getPaymentMethod(),
                receipt.getPaymentTransactionId(),
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

        // Fetch from LeadRepository instead of ProjectRepository
        List<LeadModel> projects = leadRepository.findAllByLeadIdIn(projectIds);

        List<Customer> customers = customerRepository.findAllByCustomerIdIn(customerIds);

        // Map projectId to LeadModel
        Map<String, LeadModel> projectMap = projects.stream()
                .collect(Collectors.toMap(LeadModel::getLeadId, p -> p));

        Map<String, Customer> customerMap = customers.stream()
                .collect(Collectors.toMap(Customer::getCustomerId, c -> c));

        return receipts.stream().map(receipt -> {

            LeadModel project = projectMap.get(receipt.getProjectId());
            Customer customer = customerMap.get(receipt.getCustomerId());

            ReceiptResponse.ProjectInfo projectInfo = (project != null)
                    ? new ReceiptResponse.ProjectInfo(
                    project.getLeadId(),
                    project.getProjectName()  ,
//                    null//assuming this exists in LeadModel
                    project.getAddress()
            )
                    : null;

            ReceiptResponse.CustomerInfo customerInfo = (customer != null)
                    ? new ReceiptResponse.CustomerInfo(
                    customer.getCustomerId(),
                    customer.getCustomerName()
            )
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
                    receipt.getPaymentMethod(),
                    receipt.getPaymentTransactionId(),
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

        // projectId is actually leadId in your domain
        LeadModel lead = leadRepository.findByLeadId(projectId).orElse(null);

        List<Customer> customers = customerRepository.findAllByCustomerIdIn(customerIds);

        Map<String, Customer> customerMap = customers.stream()
                .collect(Collectors.toMap(Customer::getCustomerId, c -> c));

        return receipts.stream().map(receipt -> {

            Customer customer = customerMap.get(receipt.getCustomerId());

            ReceiptResponse.ProjectInfo projectInfo = (lead != null)
                    ? new ReceiptResponse.ProjectInfo(
                    lead.getLeadId(),
                    lead.getProjectName(),
                    lead.getAddress()
            )
                    : null;

            ReceiptResponse.CustomerInfo customerInfo = (customer != null)
                    ? new ReceiptResponse.CustomerInfo(
                    customer.getCustomerId(),
                    customer.getCustomerName()
            )
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
                    receipt.getPaymentMethod(),
                    receipt.getPaymentTransactionId(),
                    linkedInvoices
            );

        }).collect(Collectors.toList());
    }


//    public UnallocatedReceiptsResponse getUnallocatedReceipts(String projectId) {
//        List<Receipt> receipts = receiptRepository.findAllByProjectId(projectId);
//
//        List<ReceiptResponse> unallocatedReceipts = receipts.stream()
//                .filter(r -> r.getAmountReceived().subtract(
//                        r.getAllocatedAmount() != null ? r.getAllocatedAmount() : BigDecimal.ZERO
//                ).compareTo(BigDecimal.ZERO) > 0)
//                .map(r -> {
//                    BigDecimal allocated = r.getAllocatedAmount() != null ? r.getAllocatedAmount() : BigDecimal.ZERO;
//                    return new ReceiptResponse(
//                            r.getId(),
//                            null,  // ProjectInfo if you want
//                            null,  // CustomerInfo if you want
//                            r.getReceiptNumber(),
//                            r.getReceiptDate(),
//                            r.getAmountReceived(),
//                            allocated,
//                            r.getAmountReceived().subtract(allocated),
//                            r.getPaymentMethod(),
//                            r.getPaymentTransactionId(),
//                            List.of()  // linkedInvoices if needed
//                    );
//                }).toList();
//
//        BigDecimal totalUnallocated = unallocatedReceipts.stream()
//                .map(ReceiptResponse::getUnallocatedAmount)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        return new UnallocatedReceiptsResponse(unallocatedReceipts, totalUnallocated);
//    }

    public UnallocatedReceiptsResponse getUnallocatedReceipts(String projectId) {
        List<Receipt> receipts = receiptRepository.findAllByProjectId(projectId);

        if (receipts.isEmpty()) {
            return new UnallocatedReceiptsResponse(List.of(), BigDecimal.ZERO);
        }

        // Fetch the associated Lead (project)
        LeadModel lead = leadRepository.findByProjectId(projectId).orElse(null);

        // Get all unique customerIds from the receipts
        Set<String> customerIds = receipts.stream()
                .map(Receipt::getCustomerId)
                .collect(Collectors.toSet());

        List<Customer> customers = customerRepository.findAllByCustomerIdIn(customerIds);
        Map<String, Customer> customerMap = customers.stream()
                .collect(Collectors.toMap(Customer::getCustomerId, c -> c));

        List<ReceiptResponse> unallocatedReceipts = receipts.stream()
                .filter(r -> r.getAmountReceived().subtract(
                        r.getAllocatedAmount() != null ? r.getAllocatedAmount() : BigDecimal.ZERO
                ).compareTo(BigDecimal.ZERO) > 0)
                .map(r -> {
                    BigDecimal allocated = r.getAllocatedAmount() != null ? r.getAllocatedAmount() : BigDecimal.ZERO;
                    Customer customer = customerMap.get(r.getCustomerId());

                    ReceiptResponse.ProjectInfo projectInfo = (lead != null)
                            ? new ReceiptResponse.ProjectInfo(lead.getProjectId(), lead.getProjectName(), lead.getAddress())
                            : null;

                    ReceiptResponse.CustomerInfo customerInfo = (customer != null)
                            ? new ReceiptResponse.CustomerInfo(customer.getCustomerId(), customer.getCustomerName())
                            : null;

                    return new ReceiptResponse(
                            r.getId(),
                            projectInfo,
                            customerInfo,
                            r.getReceiptNumber(),
                            r.getReceiptDate(),
                            r.getAmountReceived(),
                            allocated,
                            r.getAmountReceived().subtract(allocated),
                            r.getPaymentMethod(),
                            r.getPaymentTransactionId(),
                            List.of()
                    );
                }).toList();

        BigDecimal totalUnallocated = unallocatedReceipts.stream()
                .map(ReceiptResponse::getUnallocatedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new UnallocatedReceiptsResponse(unallocatedReceipts, totalUnallocated);
    }


}