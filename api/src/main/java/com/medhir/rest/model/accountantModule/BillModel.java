package com.medhir.rest.model.accountantModule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillModel {

    @Id
    @JsonIgnore
    private String id;
    @Indexed(unique = true)
    private String billId;
    private String vendorId;         // reference to Vendor

    //vendor details
    private String vendorName;       // redundant but helpful for display
    private String gstin;            // autofilled from vendor
    private String gstTreatment;     // Registered / Unregistered
    private boolean reverseCharge;

    // bill details
    private String billReference;    // invoice number
    private String billDate;
    private String dueDate;
    private String placeOfSupply;

    // company info
    private String companyId;
    private String companyName;
    private String journal;
    private String currency;
    private Status status = Status.DRAFT;           // DRAFT, POSTED, etc.
    private PaymentStatus paymentStatus = PaymentStatus.UN_PAID; // paid/pending

    private List<BillLineItem> billLineItems;

    private BigDecimal totalBeforeGST;
    private BigDecimal totalGST;
    private BigDecimal finalAmount;
    private BigDecimal totalPaid = BigDecimal.ZERO;

    // Add paymentId to link to Payment
    private String paymentId;

    //other Info
    private String paymentTerms;
    private String recipientBank;
    private String ewayBillNumber;
    private String transporter;
    private String vehicleNumber;
    private String vendorReference;
    private String shippingAddress;
    private String billingAddress;

    // attachements / notes
    private String internalNotes;
    private List<String> attachmentUrls; // if you're uploading to S3/MinIO/etc.

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillLineItem {
        private String productOrService;
        private String hsnOrSac;
        private String description;
        private int quantity;
        private String uom; // Unit of Measure
        private BigDecimal rate;
        private BigDecimal gstPercent;
        private BigDecimal discountPercent;
        private BigDecimal amount;
    }

    // computed field ;
     public BigDecimal getDueAmount() {
        if (finalAmount == null) return null;
        return finalAmount.subtract(totalPaid);
    }
    public static enum PaymentStatus {
        PAID,
        UN_PAID,
        PARTIALLY_PAID
    }
    public static enum Status {
        DRAFT,
        POSTED
    }

   
}
