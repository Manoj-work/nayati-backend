package com.medhir.rest.dto.accountantModule.expense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {
    private String expenseId;
    private EmployeeInfo createdBy;
    private VendorInfo vendor;
    private String date;
    private String expenseType;
    private String expenseCategory;
    private String projectId;
    private BigDecimal amount;
    private String notesDescription;
    private String receiptInvoiceUrl;
    private String paymentProofUrl;
    private String status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeInfo {
        private String id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorInfo {
        private String id;
        private String name;
    }
}
