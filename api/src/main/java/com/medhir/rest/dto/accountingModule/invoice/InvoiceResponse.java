package com.medhir.rest.dto.accountingModule.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {

    private String invoiceId;

    private ProjectInfo project;

    private CustomerInfo customer;

    private String invoiceNumber;

    private String invoiceDate;
    private String dueDate;

    private BigDecimal subtotal;
    private BigDecimal totalGst;
    private BigDecimal totalAmount;
    private BigDecimal amountReceived;
    private BigDecimal amountRemaining;

    private List<InvoiceItem> items;

    private String status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectInfo {
        private String projectId;
        private String projectName;
        private String siteAddress;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private String customerId;
        private String CustomerName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItem {
        private String itemName;
        private String description;
        private String hsnOrSac;
        private int quantity;
        private String uom;
        private BigDecimal rate;
        private BigDecimal gstPercentage;
        private BigDecimal total;
    }
}
