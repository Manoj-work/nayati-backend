package com.medhir.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillDTO {
    private String billId;
    private String vendorId;
    private String vendorName;
    private String gstin;
    private String vendorAddress;
    private Double tdsPercentage;
    private String billNumber;
    private String billReference;
    private String billDate;
    private String dueDate;
    private String companyId;
    private String companyName;
    private String status;
    private String paymentStatus;
    private List<BillLineItemDTO> billLineItems;
    private BigDecimal totalBeforeGST;
    private BigDecimal totalGST;
    private BigDecimal tdsApplied;
    private BigDecimal finalAmount;
    private BigDecimal totalPaid;
    private List<BillPaymentDTO> billPayments; // Multiple payments for this bill
    private List<String> attachmentUrls;
    private BigDecimal dueAmount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BillLineItemDTO {
        private String productOrService;
        private String description;
        private String hsnOrSac;
        private int quantity;
        private String uom;
        private BigDecimal rate;
        private BigDecimal amount;
        private BigDecimal gstPercent;
        private BigDecimal gstAmount;
        private BigDecimal totalAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BillPaymentDTO {
        private String paymentId;
        private BigDecimal paidAmount;
        private String paymentDate;
    }
} 