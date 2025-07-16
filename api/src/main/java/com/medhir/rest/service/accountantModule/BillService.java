package com.medhir.rest.service.accountantModule;

import com.medhir.rest.model.accountantModule.BillModel;
import com.medhir.rest.repository.accountantModule.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Collections;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.medhir.rest.service.company.CompanyService;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import com.medhir.rest.utils.MinioService;
import com.medhir.rest.dto.BillDTO;
import com.medhir.rest.model.accountantModule.BillModel.BillLineItem;
import com.medhir.rest.model.CompanyModel;
import com.medhir.rest.model.accountantModule.VendorModel;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;

    private final CompanyService companyService;

    private final VendorService vendorService;

    private final SnowflakeIdGenerator snowflakeIdGenerator;

    private final MinioService minioService;

    public BillModel createBill(BillModel bill, MultipartFile attachment) {
        // Check if company exists
        companyService.getCompanyById(bill.getCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + bill.getCompanyId()));
        // Check if vendor exists
        vendorService.getVendorById(bill.getVendorId());
        // Set bill id
        bill.setBillId("BID" + snowflakeIdGenerator.nextId());
        // Handle attachment upload
        if (attachment != null && !attachment.isEmpty()) {
            String url = minioService.uploadBillAttachment(attachment, bill.getVendorId());
            bill.setAttachmentUrls(Collections.singletonList(url));
        } else {
            bill.setAttachmentUrls(null);
        }
        return billRepository.save(bill);
    }

    public BillModel updateBill(String billId, BillModel updatedBill, MultipartFile attachment) {
        BillModel existing = billRepository.findByBillId(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + billId));
        // Check if company exists (if companyId is being updated)
        if (updatedBill.getCompanyId() != null && !updatedBill.getCompanyId().equals(existing.getCompanyId())) {
            companyService.getCompanyById(updatedBill.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + updatedBill.getCompanyId()));
        }
        // Check if vendor exists (if vendorId is being updated)
        if (updatedBill.getVendorId() != null && !updatedBill.getVendorId().equals(existing.getVendorId())) {
            vendorService.getVendorById(updatedBill.getVendorId());
        }
        // Update only non-null fields
        if (updatedBill.getCompanyId() != null) existing.setCompanyId(updatedBill.getCompanyId());
        if (updatedBill.getVendorId() != null) existing.setVendorId(updatedBill.getVendorId());
        if (updatedBill.getGstin() != null) existing.setGstin(updatedBill.getGstin());
        if (updatedBill.getVendorAddress() != null) existing.setVendorAddress(updatedBill.getVendorAddress());
        if (updatedBill.getTdsPercentage() != null) existing.setTdsPercentage(updatedBill.getTdsPercentage());
        if (updatedBill.getBillNumber() != null) existing.setBillNumber(updatedBill.getBillNumber());
        if (updatedBill.getBillReference() != null) existing.setBillReference(updatedBill.getBillReference());
        if (updatedBill.getBillDate() != null) existing.setBillDate(updatedBill.getBillDate());
        if (updatedBill.getDueDate() != null) existing.setDueDate(updatedBill.getDueDate());
        if (updatedBill.getStatus() != null) existing.setStatus(updatedBill.getStatus());
        if (updatedBill.getBillLineItems() != null) existing.setBillLineItems(updatedBill.getBillLineItems());
        if (updatedBill.getTotalBeforeGST() != null) existing.setTotalBeforeGST(updatedBill.getTotalBeforeGST());
        if (updatedBill.getTotalGST() != null) existing.setTotalGST(updatedBill.getTotalGST());
        if (updatedBill.getTdsApplied() != null) existing.setTdsApplied(updatedBill.getTdsApplied());
        if (updatedBill.getFinalAmount() != null) existing.setFinalAmount(updatedBill.getFinalAmount());
        // Handle attachment upload
        if (attachment != null && !attachment.isEmpty()) {
            String url = minioService.uploadBillAttachment(attachment, existing.getVendorId());
            existing.setAttachmentUrls(Collections.singletonList(url));
        } else if (updatedBill.getAttachmentUrls() != null) {
            existing.setAttachmentUrls(updatedBill.getAttachmentUrls());
        }
        return billRepository.save(existing);
    }

    public BillModel getBillById(String billId) {
        return billRepository.findByBillId(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + billId));
    }

    public List<BillModel> getAllBills() {
        return billRepository.findAll();
    }

    public List<BillModel> getBillsByCompanyId(String companyId) {
        // Check if company exists
        companyService.getCompanyById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));
        return billRepository.findByCompanyId(companyId);
    }

    public List<BillModel> getBillsByVendorId(String vendorId) {
        // Check if vendor exists
        vendorService.getVendorById(vendorId);
        return billRepository.findByVendorId(vendorId);
    }

    /**
     * Updates the payment details (totalPaid, paymentStatus, and paymentId) of a bill.
     * Supports multiple payments and calculates proper payment status.
     * @param billId The bill's unique ID
     * @param paidAmount The new paid amount to add
     * @param paymentId The payment's unique ID to link
     */
    public BillModel updateBillPaymentDetails(String billId, BigDecimal paidAmount, String paymentId) {
        BillModel bill = billRepository.findByBillId(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id : " + billId));
        
        BigDecimal finalAmount = bill.getFinalAmount();
        if (finalAmount == null) finalAmount = BigDecimal.ZERO;
        if (paidAmount == null) paidAmount = BigDecimal.ZERO;
        
        // Get existing bill payments or create new list
        List<BillModel.BillPayment> existingPayments = bill.getBillPayments();
        if (existingPayments == null) {
            existingPayments = new ArrayList<>();
        }
        
        // Create new payment entry
        BillModel.BillPayment newPayment = new BillModel.BillPayment();
        newPayment.setPaymentId(paymentId);
        newPayment.setPaidAmount(paidAmount);
        newPayment.setPaymentDate(java.time.LocalDate.now().toString());
        newPayment.setNotes("Payment processed");
        
        // Add new payment to list
        existingPayments.add(newPayment);
        bill.setBillPayments(existingPayments);
        
        // Calculate total paid from all payments
        BigDecimal totalPaid = existingPayments.stream()
                .map(payment -> payment.getPaidAmount() != null ? payment.getPaidAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        bill.setTotalPaid(totalPaid);
        // bill.setPaymentId(paymentId); // Keep latest payment ID for backward compatibility
        
        // Calculate payment status based on total paid vs final amount
        if (totalPaid.compareTo(finalAmount) >= 0) {
            bill.setPaymentStatus(BillModel.PaymentStatus.PAID);
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            bill.setPaymentStatus(BillModel.PaymentStatus.PARTIALLY_PAID);
        } else {
            bill.setPaymentStatus(BillModel.PaymentStatus.UN_PAID);
        }
        
        return billRepository.save(bill);
    }

    private BillDTO mapToDTO(BillModel bill) {
        CompanyModel company = companyService.getCompanyById(bill.getCompanyId()).orElse(null);
        VendorModel vendor = vendorService.getVendorById(bill.getVendorId());
        return BillDTO.builder()
                .billId(bill.getBillId())
                .vendorId(bill.getVendorId())
                .vendorName(vendor != null ? vendor.getVendorName() : null)
                .gstin(bill.getGstin())
                .vendorAddress(bill.getVendorAddress())
                .tdsPercentage(bill.getTdsPercentage())
                .billNumber(bill.getBillNumber())
                .billReference(bill.getBillReference())
                .billDate(bill.getBillDate())
                .dueDate(bill.getDueDate())
                .companyId(bill.getCompanyId())
                .companyName(company != null ? company.getName() : null)
                .status(bill.getStatus() != null ? bill.getStatus().name() : null)
                .paymentStatus(bill.getPaymentStatus() != null ? bill.getPaymentStatus().name() : null)
                .billLineItems(bill.getBillLineItems() != null ? bill.getBillLineItems().stream().map(this::mapLineItemToDTO).toList() : null)
                .totalBeforeGST(bill.getTotalBeforeGST())
                .totalGST(bill.getTotalGST())
                .tdsApplied(bill.getTdsApplied())
                .finalAmount(bill.getFinalAmount())
                .totalPaid(bill.getTotalPaid())
                .billPayments(bill.getBillPayments() != null ? bill.getBillPayments().stream().map(this::mapBillPaymentToDTO).toList() : null)
                .attachmentUrls(bill.getAttachmentUrls())
                .dueAmount(bill.getDueAmount())
                .build();
    }

    private BillDTO.BillPaymentDTO mapBillPaymentToDTO(BillModel.BillPayment billPayment) {
        return BillDTO.BillPaymentDTO.builder()
                .paymentId(billPayment.getPaymentId())
                .paidAmount(billPayment.getPaidAmount())
                .paymentDate(billPayment.getPaymentDate())
                .build();
    }

    private BillDTO.BillLineItemDTO mapLineItemToDTO(BillLineItem item) {
        return BillDTO.BillLineItemDTO.builder()
                .productOrService(item.getProductOrService())
                .description(item.getDescription())
                .hsnOrSac(item.getHsnOrSac())
                .quantity(item.getQuantity())
                .uom(item.getUom())
                .rate(item.getRate())
                .amount(item.getAmount())
                .gstPercent(item.getGstPercent())
                .gstAmount(item.getGstAmount())
                .totalAmount(item.getTotalAmount())
                .build();
    }

    public BillDTO getBillDTOById(String billId) {
        BillModel bill = getBillById(billId);
        return mapToDTO(bill);
    }

    public List<BillDTO> getAllBillDTOs() {
        return getAllBills().stream().map(this::mapToDTO).toList();
    }

    public List<BillDTO> getBillDTOsByCompanyId(String companyId) {
        return getBillsByCompanyId(companyId).stream().map(this::mapToDTO).toList();
    }

    public List<BillDTO> getBillDTOsByVendorId(String vendorId) {
        return getBillsByVendorId(vendorId).stream().map(this::mapToDTO).toList();
    }

    /**
     * Gets the payment history for a specific bill
     * @param billId The bill's unique ID
     * @return List of payment details for the bill
     */
    public List<BillDTO.BillPaymentDTO> getBillPaymentHistory(String billId) {
        BillModel bill = getBillById(billId);
        if (bill.getBillPayments() == null) {
            return new ArrayList<>();
        }
        return bill.getBillPayments().stream()
                .map(this::mapBillPaymentToDTO)
                .toList();
    }

    /**
     * Gets bills by payment status
     * @param paymentStatus The payment status to filter by
     * @return List of bills with the specified payment status
     */
    public List<BillDTO> getBillsByPaymentStatus(BillModel.PaymentStatus paymentStatus) {
        return getAllBills().stream()
                .filter(bill -> bill.getPaymentStatus() == paymentStatus)
                .map(this::mapToDTO)
                .toList();
    }

}