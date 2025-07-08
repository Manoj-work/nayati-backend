package com.medhir.rest.service.accountantModule;

import com.medhir.rest.model.accountantModule.PaymentModel;
import com.medhir.rest.repository.accountantModule.PaymentRepository;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.utils.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.web.multipart.MultipartFile;
import com.medhir.rest.dto.PaymentDTO;
import lombok.RequiredArgsConstructor;
import com.medhir.rest.service.accountantModule.VendorService;
import com.medhir.rest.model.accountantModule.VendorModel;
import com.medhir.rest.service.CompanyService;
import com.medhir.rest.model.CompanyModel;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BillService billService;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final MinioService minioService;
    private final VendorService vendorService;
    private final CompanyService companyService;

    // @Autowired
    // private BillService billService;
    // @Autowired
    // private SnowflakeIdGenerator snowflakeIdGenerator;
    // @Autowired
    // private MinioService minioService;
    // @Autowired
    // private VendorService vendorService;
    // @Autowired
    // private CompanyService companyService;

    public PaymentModel createPayment(PaymentModel payment, MultipartFile paymentProof) {
        payment.setPaymentId("PAY" + snowflakeIdGenerator.nextId());
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