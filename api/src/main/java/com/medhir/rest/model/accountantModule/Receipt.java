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
@Document(collection = "receipts")
public class Receipt {

    @JsonIgnore
    @Id
    private String id;

    @Indexed(unique = true)
    private String receiptNumber;  // Unique number

    @Indexed
    private String projectId;      // Link to Project
    private String customerId;     // Link to Customer


    private String receiptDate;    // ISO String or LocalDate

    private BigDecimal amountReceived;

    private String paymentMethod;
    private String bankAccountId;
    private String paymentTransactionId;
    private BigDecimal allocatedAmount;

    @Builder.Default
    private List<LinkedInvoice> linkedInvoices = new ArrayList<>();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkedInvoice {
        private String invoiceNumber;
        private BigDecimal amountAllocated;
    }

    public BigDecimal getUnallocatedAmount() {
        if (amountReceived == null) return BigDecimal.ZERO;
        if (allocatedAmount == null) return amountReceived;
        return amountReceived.subtract(allocatedAmount);
    }
}
