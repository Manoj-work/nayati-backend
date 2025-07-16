package com.medhir.rest.sales.model;

import com.medhir.rest.utils.GeneratedId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "leads")
public class LeadModel {
    @Setter
    @Transient
    private GeneratedId generatedId;
    @Id
    @JsonIgnore
    private String id;
    @Indexed(unique = true)
    private String leadId;
    private String customerId;
    private String projectName;
    private String projectId;
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
    @Indexed
    private String priority;
    @Indexed
    private String salesRep;
    @Indexed
    private String designer;
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
    @Indexed
    private String dateOfCreation;

    private List<Activity> activities = new ArrayList<>();
    private List<Note> notesList = new ArrayList<>();
    private List<ActivityLog> activityLogs = new ArrayList<>();

    public void generateProjectName() {
        if (this.projectName == null && this.generatedId != null) {
            this.projectName = generatedId.generateId("PROJ-", LeadModel.class, "projectName");
        }
    }
}