package com.medhir.rest.dto.filter;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateIncomeStatusRequest {
    @NotBlank(message = "Status is required")
    private String status;
    
    private String remarks;
}
