package com.medhir.rest.sales.dto.lead;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadRequestDTO {
    private String name;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    private String contactNumber;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String projectType;
    private String propertyType;
    private String address;
    private String area;
    private String budget;
    private String designStyle;
    private String leadSource;
    private String notes;
    private String stageId; // Changed from status to stageId for better data integrity
    private Integer rating;
    private String priority; // Added: Priority as string (Low, Medium, High)
    private String salesRep;
    private String designer;
    private String callDescription;
    private List<String> callHistory = new ArrayList<>();

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$",
            message = "Next call must be in yyyy-MM-dd'T'HH:mm:ss format"
    )
    private String nextCall;

    private String quotedAmount;
    private String finalQuotation;
    private String signupAmount;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Payment date must be in yyyy-MM-dd format")
    private String paymentDate;

    private String paymentMode;
    private String panNumber;
    private String discount;
    private String reasonForLost;
    private String reasonForJunk;
    private String submittedBy;
    private String paymentDetailsFileName;
    private String bookingFormFileName;
    private String initialQuote;
    private String projectTimeline;

    private String assignedSalesRep;  // Optional field for assignment
    private String assignedDesigner;  // Optional field for assignment
    private String dateOfCreation; // Added: Date of creation (ISO date string)
} 