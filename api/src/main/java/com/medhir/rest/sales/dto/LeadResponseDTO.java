package com.medhir.rest.sales.dto;

import com.medhir.rest.sales.ModelLead;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LeadResponseDTO {
    // Lead identifiers and main info
    private String leadId;
    private String name;
    private String email;
    private String contactNumber;
    private String projectType;
    private String propertyType;
    private String address;
    private Double budget;
    private String leadSource;
    private String designStyle;
    private String priority;
    private String dateOfCreation;

    // Employee info
    private String createdById;
    private String createdByName;

    private String assignedSalesPersonId;
    private String assignedSalesPersonName;

    private String assignedDesignerId;
    private String assignedDesignerName;

    // Stage info
    private String stageId;
    private String stageName;

    // Financial info
    private String initialQuotedAmount;
    private String finalQuotation;
    private String signUpAmount;
    private String paymentDate;
    private String paymentMode;
    private String panNumber;
    private String projectTimeline;
    private BigDecimal discount;
    private String paymentDetailsFile;
    private String bookingFormFile;

    // Status/reasons
    private String reasonForLoss;
    private String reasonForMarkingAsJunk;

    // Nested objects
    private List<ActivityDetailsDTO> activities;
    private List<NoteDTO> notes;
    private List<ActivityLogEntryDTO> activityLog;

    // ---- Nested DTOs ----

    @Getter
    @Setter
    public static class ActivityDetailsDTO {
        private String activityId;
        private String type;             // To-Do, Email, etc.
        private String title;
        private String purposeOfTheCall;
        private String outComeOfTheCall;
        private String dueDate;
        private String time;
        private LocalDateTime nextFollowUp;
        private String assignedTo;
        private String status;
        private String meetingVenue;
        private String meetingLink;
        private String outcomeOfTheMeeting;
        private List<String> attendees;
        private String attach;
    }

    @Getter
    @Setter
    public static class NoteDTO {
        private String noteId;
        private String note;
        private String user;
        private String timestamp;
    }

    @Getter
    @Setter
    public static class ActivityLogEntryDTO {
        private String logId;
        private String type;
        private String previousStageId;
        private String previousStageName;
        private String newStageId;
        private String newStageName;
        private String summary;
        private String title;
        private String performedBy;
        private String timestamp;
    }
}
