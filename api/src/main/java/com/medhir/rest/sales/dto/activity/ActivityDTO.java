package com.medhir.rest.sales.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDTO {
    @NotBlank(message = "Activity ID is required")
    private String id;
    @NotBlank(message = "Activity type is required")
    private String type;
    @NotBlank(message = "Activity summary is required")
    private String summary;
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Due date must be in yyyy-MM-dd format")
    private String dueDate;
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Due time must be in HH:mm format")
    private String dueTime;
    private String user;
    private String status; // Dynamic activity status
    private String meetingLink;
    private List<String> attendees = new ArrayList<>();
    private String callPurpose;
    private String callOutcome;
    private String nextFollowUpDate;
    private String nextFollowUpTime;
    private String meetingVenue;
    private String note; // Added: Activity-specific note
    private String attachment; // Added: Activity-specific attachment (file name or URL)
} 