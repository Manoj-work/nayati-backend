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
    private String gstTreatment;
    private boolean reverseCharge;
    private String billReference;
    private String billDate;
    private String dueDate;
    private String placeOfSupply;
    private String companyId;
    private String companyName;
    private String journal;
    private String currency;
    private String status;
    private String paymentStatus;
    private List<BillLineItemDTO> billLineItems;
    private BigDecimal totalBeforeGST;
    private BigDecimal totalGST;
    private BigDecimal finalAmount;
    private BigDecimal totalPaid;
    private String paymentId;
    private String paymentTerms;
    private String recipientBank;
    private String ewayBillNumber;
    private String transporter;
    private String vehicleNumber;
    private String vendorReference;
    private String shippingAddress;
    private String billingAddress;
    private String internalNotes;
    private List<String> attachmentUrls;
    private BigDecimal dueAmount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BillLineItemDTO {
        private String productOrService;
        private String hsnOrSac;
        private String description;
        private int quantity;
        private String uom;
        private BigDecimal rate;
        private BigDecimal gstPercent;
        private BigDecimal discountPercent;
        private BigDecimal amount;
    }
} 