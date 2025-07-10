package com.medhir.rest.dto.accountantModule.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExpenseRequest {
    @NotBlank
    private String createdBy;
    @NotBlank private String date;
    @NotBlank private String expenseType;
    @NotBlank private String expenseCategory;
    @NotBlank private String projectId;
    @NotBlank private String vendorId;
    @NotNull  private BigDecimal amount;
    @NotBlank private String notesDescription;
}

