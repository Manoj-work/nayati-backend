package com.medhir.rest.sales.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    private String id;
    private String type;
    private String title;
    private String notes;
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