package com.medhir.rest.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private String paymentId;
    private String vendorId;
    private String vendorName;
    private String companyId;
    private String companyName;
    private String gstin;
    private String paymentMethod;
    private String bankAccount;
    private String paymentTransactionId;
    private String paymentDate;
    private BigDecimal totalAmount;
    private boolean tdsApplied;
    private String notes;
    private String paymentProofUrl;
    private List<BillPaymentDTO> billPayments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillPaymentDTO {
        private String billId;
        private BigDecimal paidAmount;
    }
}
