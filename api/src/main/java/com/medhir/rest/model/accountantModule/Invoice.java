package com.medhir.rest.model.accountantModule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "invoices")
public class Invoice {

//    @JsonIgnore
    @Id
    private String id;

    @Indexed(unique = true)
    private String invoiceNumber;

    @Indexed
    private String projectId;      // Reference to the Project
    private String customerId;     // Reference to the Customer

    private String invoiceDate;
    private String dueDate;

    private List<InvoiceItem> items;

    private BigDecimal subtotal;
    private BigDecimal totalGst;
    private BigDecimal totalAmount;
    private BigDecimal amountReceived;
    @Builder.Default
    private Status status = Status.PENDING;
    @Builder.Default
    private List<LinkedReceipt> linkedReceipts = new ArrayList<>();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkedReceipt {
        private String receiptNumber;
        private BigDecimal amountAllocated;
    }

    public BigDecimal getAmountRemaining() {
        // If amountReceived is null → treat it as ZERO
        BigDecimal received = amountReceived == null ? BigDecimal.ZERO : amountReceived;
        return totalAmount.subtract(received);
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InvoiceItem {
        private String itemName;     // Product or Service
        private String description;
        private String hsnOrSac;     // HSN/SAC code
        private int quantity;
        private String uom;          // Unit of Measure
        private double rate;
        private double gstPercentage;
        private double total;        // Total for this line item
    }

    public static enum Status {
        PENDING, PAID,PARTIALLYPAID
    }

}