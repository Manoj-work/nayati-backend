package com.medhir.rest.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "expenses")
public class Expense {

    @Id
    private String id;

    @Indexed(unique = true)
    private String expenseId;

    @NotBlank(message = "Expense type is required")
    private String expenseType;

    @NotBlank(message = "Client name is required")
    private String clientName;

    @NotBlank(message = "Project ID is required")
    private String projectId;

    @NotBlank(message = "Expense category is required")
    private String expenseCategory;

    @NotBlank(message = "Vendor name is required")
    private String vendorName;

    @NotNull(message = "Total expense amount is required")
    private BigDecimal totalExpenseAmount;

    @NotNull(message = "Reimbursement amount is required")
    private BigDecimal reimbursementAmount;

    @NotBlank(message = "GST credit available is required")
    private String gstCredit;

    @NotBlank(message = "Receipt/Invoice attachment URL is required")
    private String receiptInvoiceAttachmentUrl;

    @NotBlank(message = "Notes/Description is required")
    private String notesDescription;

    @NotBlank(message = "Status is required")
    private String status = "pending"; //

    private String paymentProof;

    private String rejectionComment;

}
