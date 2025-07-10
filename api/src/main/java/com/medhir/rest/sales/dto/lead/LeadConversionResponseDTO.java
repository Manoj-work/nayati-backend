package com.medhir.rest.sales.dto.lead;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadConversionResponseDTO {
    private String leadId;
    private String leadName;
    private String oldStatus;
    private String newStatus;
    private String finalQuotation;
    private String signupAmount;
    private String paymentDate;
    private String paymentMode;
    private String panNumber;
    private String discount;
    private String initialQuote;
    private String projectTimeline;
    private String conversionNoteId;
    private String conversionNoteContent;
    private String convertedBy;
    private String conversionTimestamp;
    private String message;
} 