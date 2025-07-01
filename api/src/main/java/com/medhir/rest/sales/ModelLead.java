package com.medhir.rest.sales;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.medhir.rest.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medhir.rest.model.EmployeeModel;

import java.math.BigDecimal;// exact amount and no loss of decimal places
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "leads")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ModelLead {

    @Id
    @JsonIgnore
    private String id;

    @Indexed(unique = true)
    private String leadId;

    private String managerId;
    private String employeeId;
    private String stageId;
    private String stageName;


    @NotBlank(message = "Name is required")
    private String name;

    @Indexed(unique = true)
    @NotNull(message = "Email is required!")
    @Email(message = "Invalid email format")
    private String email = "";

    @Indexed(unique = true)
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String contactNumber = "";

    @NotBlank(message = "Project Type is required")
    private String projectType = "";

    @JsonProperty(required = false)
    private String propertyType = "";

    private String address = "";
    private Double budget;

    @NotBlank(message = "Source is required")
    private String leadSource = "";

//    @JsonProperty(required = false)
//    private String status = "New";

    private String designStyle;
//    private Role role;
    private String assignedSalesPerson;
    private String assignedDesigner;
    private String priority;
    private String dateOfCreation;
    // Nested schedule activity class
    private List<ActivityDetails> activities = new ArrayList<>();

    // Changed from String to List<Note>
    private List<Note> notes = new ArrayList<>();

    private List<ActivityLogEntry> activityLog = new ArrayList<>();




    @Getter
    @Setter
    public static class ActivityDetails {


        @Indexed(unique = true)
        private String activityId;
        private String type;             // To-Do, Email, etc.
        private String title;
        private String purposeOfTheCall;
        private String outComeOfTheCall;
        private String dueDate;
        private String time;
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
        private LocalDateTime nextFollowUp;
        private String assignedTo;
        private String status;
        private String meetingVenue;
        private String meetingLink;
        private List<String> attendees;
        private String attach;
    }
    // For "Converted"
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

    // For "Lost"
    private String reasonForLoss;

    // For "Junk"
    private String reasonForMarkingAsJunk;

    @Getter
    @Setter
    public static class Note {


        @JsonIgnore
        private String noteId;
        private String content;
        private String user;
        private String timestamp;
    }

    @Getter
    @Setter
    public static class ActivityLogEntry {

        @Indexed(unique = true)
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





