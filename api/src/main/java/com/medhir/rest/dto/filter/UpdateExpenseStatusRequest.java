package com.medhir.rest.dto.filter;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateExpenseStatusRequest {
    @NotBlank(message = "Status is required")
    private String status;
    
    private String remarks;
} 