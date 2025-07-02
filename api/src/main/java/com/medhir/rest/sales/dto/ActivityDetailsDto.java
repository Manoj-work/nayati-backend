package com.medhir.rest.sales.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityDetailsDto {
    private String type;
    private String title;
    private String purposeOfTheCall;
    private String outComeOfTheCall;
    private String dueDate;
    private String time;
    private LocalDateTime nextFollowUp;
    private String assignedTo;
    private String status;
    private String outcomeOfTheMeeting;
    private String meetingVenue;
    private String meetingLink;
    private List<String> attendees;
    private MultipartFile attach; // For file upload
}

