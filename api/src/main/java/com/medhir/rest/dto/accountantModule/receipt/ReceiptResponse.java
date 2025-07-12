package com.medhir.rest.dto.accountantModule.receipt;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiptResponse {

    private String id;

    private ProjectInfo project;

    private CustomerInfo customer;

    private String receiptNumber;

    private String receiptDate;

    private BigDecimal amountReceived;

    private BigDecimal allocatedAmount;

    private BigDecimal unallocatedAmount;

    private String paymentMethod;

    private List<LinkedInvoice> linkedInvoices;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProjectInfo {
        private String projectId;
        private String projectName;
        private String siteAddress;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerInfo {
        private String customerId;
        private String customerName;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkedInvoice {
        private String invoiceNumber;
        private BigDecimal amountAllocated;
    }
}
