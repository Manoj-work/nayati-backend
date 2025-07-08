package com.medhir.rest.model.accountantModule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document(collection = "expenses")
public class Expense {
    @JsonIgnore
    @Id
    private String id;

    @Indexed(unique = true)
    private String expenseId;

    @Indexed
    private String createdBy;
    private String date;
    private String expenseType;
    private String expenseCategory;
    @Indexed
    private String projectId;
    @Indexed
    private String vendorId;
    private BigDecimal amount;
    private String notesDescription;
    private String receiptInvoiceUrl;
    private String paymentProofUrl;

    private Status status = Status.PENDING;

    public static enum Status {
        PENDING, PAID
    }
}
