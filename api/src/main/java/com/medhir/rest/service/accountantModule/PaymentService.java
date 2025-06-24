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

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BillService billService;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private MinioService minioService;

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
}