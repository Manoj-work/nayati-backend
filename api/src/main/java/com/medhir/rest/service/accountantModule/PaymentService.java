package com.medhir.rest.service.accountantModule;

import com.medhir.rest.model.accountantModule.PaymentModel;
import com.medhir.rest.repository.accountantModule.PaymentRepository;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.utils.MinioService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.web.multipart.MultipartFile;
import com.medhir.rest.dto.PaymentDTO;
import lombok.RequiredArgsConstructor;
import com.medhir.rest.model.accountantModule.VendorModel;
import com.medhir.rest.service.company.CompanyService;
import com.medhir.rest.model.CompanyModel;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BillService billService;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final MinioService minioService;
    private final VendorService vendorService;
    private final CompanyService companyService;

    public PaymentModel createPayment(PaymentModel payment, MultipartFile paymentProof) {
        payment.setPaymentId("PAY" + snowflakeIdGenerator.nextId());
        
        // Handle vendor credit adjustment if adjustedAmountFromCredits is provided
        if (payment.getAdjustedAmountFromCredits() != null && 
            payment.getAdjustedAmountFromCredits().compareTo(BigDecimal.ZERO) > 0) {
            
            // Validate that adjusted amount doesn't exceed total payment amount
            // if (payment.getTotalAmount() != null && 
            //     payment.getAdjustedAmountFromCredits().compareTo(payment.getTotalAmount()) > 0) {
            //     throw new IllegalArgumentException("Adjusted amount from credits cannot exceed total payment amount. " +
            //         "Total: " + payment.getTotalAmount() + ", Adjusted: " + payment.getAdjustedAmountFromCredits());
            // }
            
            // Validate vendor exists
            VendorModel vendor = vendorService.getVendorById(payment.getVendorId());
            
            // Check if vendor has sufficient credits
            if (vendor.getTotalCredit() == null || 
                vendor.getTotalCredit().compareTo(payment.getAdjustedAmountFromCredits()) < 0) {
                throw new IllegalArgumentException("Insufficient vendor credits. Available: " + 
                    (vendor.getTotalCredit() != null ? vendor.getTotalCredit() : BigDecimal.ZERO) + 
                    ", Requested: " + payment.getAdjustedAmountFromCredits());
            }
            
            // Create new adjusted payment entry
            VendorModel.AdjustedPayments adjustedPayment = new VendorModel.AdjustedPayments();
            adjustedPayment.setAdjustedAmount(payment.getAdjustedAmountFromCredits().toString());
            adjustedPayment.setAdjustedDate(payment.getPaymentDate());
            adjustedPayment.setAdjustedPaymentId(payment.getPaymentId());
            
            // Get existing adjusted payments or create new list
            List<VendorModel.AdjustedPayments> existingAdjustedPayments = vendor.getAdjustedPayments();
            if (existingAdjustedPayments == null) {
                existingAdjustedPayments = new ArrayList<>();
            }
            existingAdjustedPayments.add(adjustedPayment);
            vendor.setAdjustedPayments(existingAdjustedPayments);
            
            // Update vendor's total credit by subtracting the adjusted amount
            BigDecimal newTotalCredit = vendor.getTotalCredit().subtract(payment.getAdjustedAmountFromCredits());
            vendor.setTotalCredit(newTotalCredit);
            
            // Save updated vendor
            vendorService.updateVendorCreditsFromPayment(vendor);
        }
        
        // Handle payment proof upload
        if (paymentProof != null && !paymentProof.isEmpty()) {
            String url = minioService.uploadPaymentProof(paymentProof, payment.getPaymentId());
            payment.setPaymentProofUrl(url);
        }
        
        // Update each bill with paid amount and status
        if (payment.getBillPayments() != null) {
            for (PaymentModel.BillPaymentDetail detail : payment.getBillPayments()) {
                billService.updateBillPaymentDetails(detail.getBillId(), detail.getPaidAmount(), payment.getPaymentId());
            }
        }
        
        return paymentRepository.save(payment);
    }

    public List<PaymentModel> getAllPayments() {
        return paymentRepository.findAll();
    }

    public PaymentModel getPaymentByPaymentId(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(()-> new ResourceNotFoundException("Payment not found with id : " + paymentId));
    }

    public PaymentDTO getPaymentDTOById(String paymentId) {
        PaymentModel payment = getPaymentByPaymentId(paymentId);
        return mapToDTO(payment);
    }

    public List<PaymentDTO> getAllPaymentDTOs() {
        return getAllPayments().stream().map(this::mapToDTO).toList();
    }

    private PaymentDTO mapToDTO(PaymentModel payment) {
        VendorModel vendor = null;
        String vendorName = null;
        CompanyModel company = null;
        String companyName = null;
        String gstin = payment.getGstin();
        if (payment.getVendorId() != null) {
            try {
                vendor = vendorService.getVendorById(payment.getVendorId());
                company = companyService.getCompanyById(payment.getCompanyId()).orElse(null);
                if (vendor != null) {
                    vendorName = vendor.getVendorName();
                    companyName = company.getName();
                    gstin = vendor.getGstin();
                }
            } catch (ResourceNotFoundException e) {
                // vendorName remains null, gstin remains as in payment
            }
        }
        return PaymentDTO.builder()
                .paymentId(payment.getPaymentId())
                .vendorId(payment.getVendorId())
                .vendorName(vendorName)
                .companyId(payment.getCompanyId())
                .companyName(companyName)
                .gstin(gstin)
                .paymentMethod(payment.getPaymentMethod())
                .bankAccount(payment.getBankAccount())
                .paymentTransactionId(payment.getPaymentTransactionId())
                .paymentDate(payment.getPaymentDate())
                .totalAmount(payment.getTotalAmount())
                .adjustedAmountFromCredits(payment.getAdjustedAmountFromCredits())
                .tdsApplied(payment.isTdsApplied())
                .notes(payment.getNotes())
                .paymentProofUrl(payment.getPaymentProofUrl())
                .billPayments(payment.getBillPayments().stream()
                        .map(this::mapBillPayments)
                        .toList())
                .build();
    }

    private PaymentDTO.BillPaymentDTO mapBillPayments(PaymentModel.BillPaymentDetail billPayments) {
        return PaymentDTO.BillPaymentDTO.builder()
                .billId(billPayments.getBillId())
                .paidAmount(billPayments.getPaidAmount())
                .build();
    }
}