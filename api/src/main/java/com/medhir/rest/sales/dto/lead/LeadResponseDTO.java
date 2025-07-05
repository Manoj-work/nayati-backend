package com.medhir.rest.sales.dto.lead;

import com.medhir.rest.sales.dto.activity.ActivityDTO;
import com.medhir.rest.sales.dto.activity.NoteDTO;
import com.medhir.rest.sales.dto.activity.ActivityLogDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadResponseDTO {
    private String leadId;
    private String name;
    private String contactNumber;
    private String email;
    private String projectType;
    private String propertyType;
    private String address;
    private String area;
    private String budget;
    private String designStyle;
    private String leadSource;
    private String notes;
    private String stageId;
    private String stageName;
    private String stageColor;
    private String priority;
    private String salesRep;
    private String salesRepName;
    private String designer;
    private String designerName;
    private String createdBy;
    private String createdByName;
    private String quotedAmount;
    private String finalQuotation;
    private String signupAmount;
    private String paymentDate;
    private String paymentMode;
    private String panNumber;
    private String discount;
    private String reasonForLost;
    private String reasonForJunk;
    private String paymentDetailsFileName;
    private String bookingFormFileName;
    private String initialQuote;
    private String projectTimeline;
//    private String assignedSalesRep;
//    private String assignedDesigner;
    private String createdAt;
    private String updatedAt;
    private String dateOfCreation;
    private List<ActivityDTO> activities;
    private List<NoteDTO> notesList;
    private List<ActivityLogDTO> activityLogs;

//    private salesInfo salesinfo;
//    public static class SalesInfo {
//        private String slaesID;
//        private String SalesName;
//
//    }
} 