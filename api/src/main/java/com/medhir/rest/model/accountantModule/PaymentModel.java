package com.medhir.rest.model.accountantModule;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentModel {

    @Id
    private String id;

    @Indexed(unique = true)
    private String paymentId;

    private String vendorId; // mapped to vendor
    private String companyId; // mapped to company

    private String journal;
    private String paymentMethod;
    private String bankAccount;
    private String currency = "INR";

    private String paymentDate;
    private BigDecimal totalAmount;

    private boolean tdsApplied;
    private String reference;
    private String notes;
    private String paymentProofUrl;

    private List<BillPaymentDetail> billPayments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillPaymentDetail {
        private String billId;
        private BigDecimal paidAmount;
    }
}
