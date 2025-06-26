package com.medhir.rest.service.accountantModule;

import com.medhir.rest.model.accountantModule.BillModel;
import com.medhir.rest.repository.accountantModule.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Collections;
import java.math.BigDecimal;

import com.medhir.rest.service.CompanyService;
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
//        if (updatedBill.getCompanyName() != null) existing.setCompanyName(updatedBill.getCompanyName());
        if (updatedBill.getVendorId() != null) existing.setVendorId(updatedBill.getVendorId());
//        if (updatedBill.getVendorName() != null) existing.setVendorName(updatedBill.getVendorName());
        if (updatedBill.getGstin() != null) existing.setGstin(updatedBill.getGstin());
        if (updatedBill.getGstTreatment() != null) existing.setGstTreatment(updatedBill.getGstTreatment());
        existing.setReverseCharge(updatedBill.isReverseCharge());
        if (updatedBill.getBillReference() != null) existing.setBillReference(updatedBill.getBillReference());
        if (updatedBill.getBillDate() != null) existing.setBillDate(updatedBill.getBillDate());
        if (updatedBill.getDueDate() != null) existing.setDueDate(updatedBill.getDueDate());
        if (updatedBill.getPlaceOfSupply() != null) existing.setPlaceOfSupply(updatedBill.getPlaceOfSupply());
        if (updatedBill.getJournal() != null) existing.setJournal(updatedBill.getJournal());
        if (updatedBill.getCurrency() != null) existing.setCurrency(updatedBill.getCurrency());
        if (updatedBill.getStatus() != null) existing.setStatus(updatedBill.getStatus());
        if (updatedBill.getBillLineItems() != null) existing.setBillLineItems(updatedBill.getBillLineItems());
        if (updatedBill.getTotalBeforeGST() != null) existing.setTotalBeforeGST(updatedBill.getTotalBeforeGST());
        if (updatedBill.getTotalGST() != null) existing.setTotalGST(updatedBill.getTotalGST());
        if (updatedBill.getFinalAmount() != null) existing.setFinalAmount(updatedBill.getFinalAmount());
        if (updatedBill.getPaymentTerms() != null) existing.setPaymentTerms(updatedBill.getPaymentTerms());
        if (updatedBill.getRecipientBank() != null) existing.setRecipientBank(updatedBill.getRecipientBank());
        if (updatedBill.getEwayBillNumber() != null) existing.setEwayBillNumber(updatedBill.getEwayBillNumber());
        if (updatedBill.getTransporter() != null) existing.setTransporter(updatedBill.getTransporter());
        if (updatedBill.getVehicleNumber() != null) existing.setVehicleNumber(updatedBill.getVehicleNumber());
        if (updatedBill.getVendorReference() != null) existing.setVendorReference(updatedBill.getVendorReference());
        if (updatedBill.getShippingAddress() != null) existing.setShippingAddress(updatedBill.getShippingAddress());
        if (updatedBill.getBillingAddress() != null) existing.setBillingAddress(updatedBill.getBillingAddress());
        if (updatedBill.getInternalNotes() != null) existing.setInternalNotes(updatedBill.getInternalNotes());
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
     * @param billId The bill's unique ID
     * @param paidAmount The new paid amount to set
     * @param paymentId The payment's unique ID to link
     */
    public BillModel updateBillPaymentDetails(String billId, BigDecimal paidAmount, String paymentId) {
        BillModel bill = billRepository.findByBillId(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id : " + billId));
        BigDecimal finalAmount = bill.getFinalAmount();
        if (paidAmount == null) paidAmount = BigDecimal.ZERO;
        if (finalAmount == null) finalAmount = BigDecimal.ZERO;
        bill.setTotalPaid(paidAmount);
        bill.setPaymentId(paymentId);
        if (paidAmount.compareTo(finalAmount) >= 0) {
            bill.setPaymentStatus(BillModel.PaymentStatus.PAID);
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
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
                .gstTreatment(bill.getGstTreatment())
                .reverseCharge(bill.isReverseCharge())
                .billReference(bill.getBillReference())
                .billDate(bill.getBillDate())
                .dueDate(bill.getDueDate())
                .placeOfSupply(bill.getPlaceOfSupply())
                .companyId(bill.getCompanyId())
                .companyName(company != null ? company.getName() : null)
                .journal(bill.getJournal())
                .currency(bill.getCurrency())
                .status(bill.getStatus() != null ? bill.getStatus().name() : null)
                .paymentStatus(bill.getPaymentStatus() != null ? bill.getPaymentStatus().name() : null)
                .billLineItems(bill.getBillLineItems() != null ? bill.getBillLineItems().stream().map(this::mapLineItemToDTO).toList() : null)
                .totalBeforeGST(bill.getTotalBeforeGST())
                .totalGST(bill.getTotalGST())
                .finalAmount(bill.getFinalAmount())
                .totalPaid(bill.getTotalPaid())
                .paymentId(bill.getPaymentId())
                .paymentTerms(bill.getPaymentTerms())
                .recipientBank(bill.getRecipientBank())
                .ewayBillNumber(bill.getEwayBillNumber())
                .transporter(bill.getTransporter())
                .vehicleNumber(bill.getVehicleNumber())
                .vendorReference(bill.getVendorReference())
                .shippingAddress(bill.getShippingAddress())
                .billingAddress(bill.getBillingAddress())
                .internalNotes(bill.getInternalNotes())
                .attachmentUrls(bill.getAttachmentUrls())
                .dueAmount(bill.getDueAmount())
                .build();
    }

    private BillDTO.BillLineItemDTO mapLineItemToDTO(BillLineItem item) {
        return BillDTO.BillLineItemDTO.builder()
                .productOrService(item.getProductOrService())
                .hsnOrSac(item.getHsnOrSac())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .uom(item.getUom())
                .rate(item.getRate())
                .gstPercent(item.getGstPercent())
                .discountPercent(item.getDiscountPercent())
                .amount(item.getAmount())
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

}