package com.medhir.rest.sales.dto.lead;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadAssignmentRequestDTO {
    
    @NotBlank(message = "Assigned Sales Rep is required")
    private String assignedSalesRep;
    
    @NotBlank(message = "Assigned Designer is required") 
    private String assignedDesigner;
    
    private String assignmentNotes; // Optional reason for change
} 