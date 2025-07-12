package com.medhir.rest.sales.dto.lead;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvertLeadRequestDTO {
    
    @NotBlank(message = "Final Quotation is required")
    private String finalQuotation;
    
    @NotBlank(message = "Sign-up Amount is required")
    private String signupAmount;
    
    @NotBlank(message = "Payment Date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Payment Date must be in yyyy-MM-dd format")
    private String paymentDate;
    
    @NotBlank(message = "Payment Mode is required")
    private String paymentMode;
    
    @NotBlank(message = "PAN Number is required")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "PAN Number must be in valid format (e.g., ABCDE1234F)")
    private String panNumber;
    
    private String discount;
    
    @NotBlank(message = "Initial Quote is required")
    private String initialQuote;
    
    @NotBlank(message = "Project Timeline is required")
    private String projectTimeline;
    
    private String paymentDetailsFileName;
    private String bookingFormFileName;
    
    @NotBlank(message = "Conversion Notes are required")
    private String conversionNotes;
} 