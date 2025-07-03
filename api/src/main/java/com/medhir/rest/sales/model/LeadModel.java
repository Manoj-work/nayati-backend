package com.medhir.rest.sales.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "leads")
public class LeadModel {
    @Id
    @JsonIgnore
    private String id;
    @Indexed(unique = true)
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
    private Integer rating;
    private String priority;
    private String salesRep;
    private String designer;
    private String callDescription;
    private List<String> callHistory = new ArrayList<>();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String nextCall;

    private String quotedAmount;
    private String finalQuotation;
    private String signupAmount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
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

    private String assignedSalesRep;
    private String assignedDesigner;
    private String dateOfCreation;

    private List<Activity> activities = new ArrayList<>();
    private List<Note> notesList = new ArrayList<>();
    private List<ActivityLog> activityLogs = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Activity {
        private String id;
        private String type;
        private String summary;
        private String dueDate;
        private String dueTime;
        private String user;
        private String status;
        private String meetingLink;
        private List<String> attendees;
        private String callPurpose;
        private String callOutcome;
        private String nextFollowUpDate;
        private String nextFollowUpTime;
        private String meetingVenue;
        private String note;
        private String attachment;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Note {
        private String id;
        private String content;
        private String user;
        private String timestamp;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityLog {
        private String id;
        private String action;
        private String details;
        private String user;
        private String timestamp;
        private String activityType;
        private Map<String, Object> metadata = new HashMap<>();
    }
}