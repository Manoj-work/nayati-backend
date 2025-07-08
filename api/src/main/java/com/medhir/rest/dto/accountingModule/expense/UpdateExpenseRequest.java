package com.medhir.rest.dto.accountingModule.expense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExpenseRequest {
    private String createdBy;
    private String date;
    private String expenseType;
    private String expenseCategory;
    private String projectId;
    private String vendorId;
    private BigDecimal amount;
    private String notesDescription;
}
