package com.medhir.rest.sales;

import com.medhir.rest.repository.EmployeeRepository;
import com.medhir.rest.sales.dto.ActivityDetailsDto;
import com.medhir.rest.utils.MinioService;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.medhir.rest.service.EmployeeService;
import java.util.Iterator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LeadService {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    private MinioService minioService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    // -------------------- Lead Creation --------------------
    @Autowired
    private PipelineStageRepository pipelineStageRepository;


public ModelLead createLead(ModelLead lead) {
    // Always assign the "New" stage
    PipelineStageModel stage = pipelineStageRepository.findByStageNameIgnoreCase("New")
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Default stage 'New' not found"));

    // Set stage details on the lead
    lead.setStageId(stage.getStageId());
    lead.setStageName(stage.getStageName());

    try {
        lead.setLeadId("LID" + snowflakeIdGenerator.nextId());

        // Handle notes
        if (lead.getNotes() != null) {
            for (ModelLead.Note note : lead.getNotes()) {
                note.setNoteId("NID" + snowflakeIdGenerator.nextId());
//                note.setUser(lead.getRole() != null ? lead.getRole().name() : "User");
                if (note.getTimestamp() == null) {
                    note.setTimestamp(LocalDateTime.now().toString());
                }
            }
        }

        // Log creation in activity log
//        addActivityLogEntry(
//                lead,
//                "lead",
//                null,       // previousStageId (no previous stage)
//                null,       // previousStageName
//                stage.getStageId(),  // newStageId (current stage)
//                stage.getStageName(), // newStageName
//                "Lead created in stage: " + stage.getStageName()
//
//        );

        return leadRepository.save(lead);

    } catch (DuplicateKeyException e) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Duplicate key: phone number or email already exists");
    } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error creating lead: " + e.getMessage());
    }
}


    // -------------------- Lead Retrieval --------------------
//    public List<ModelLead> getAllLeads(String managerId) {
//        if (!leadRepository.existsByManagerId(managerId)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Manager ID not found in any lead");
//        }
//
//        return leadRepository.findByManagerId(managerId);
//    }

    public List<ModelLead> getAllLeads() {
        return leadRepository.findAll();
    }


    public Optional<ModelLead> getLeadByLeadId(String leadId) {
        return leadRepository.findByLeadId(leadId);
    }

    //--------------Find Lead by Employee ID
    public List<ModelLead> getLeadsByEmployeeId(String employeeId) {
        return leadRepository.findByAssignedSalesPersonOrAssignedDesigner(employeeId, employeeId);
    }



    public List<ModelLead> getLeadsByStageId(String stageId) {
        return leadRepository.findByStageId(stageId);
    }


    // -------------------- Lead Update --------------------
    public ModelLead updateLead(String leadId, ModelLead updatedLead) {
        ModelLead existing = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead with ID '" + leadId + "' not found"));

        // Only update fields that are meant to be changed
        existing.setName(updatedLead.getName());
        existing.setEmail(updatedLead.getEmail());
        existing.setContactNumber(updatedLead.getContactNumber());
        existing.setProjectType(updatedLead.getProjectType());
        existing.setPropertyType(updatedLead.getPropertyType());
        existing.setAddress(updatedLead.getAddress());
        existing.setBudget(updatedLead.getBudget());
        existing.setLeadSource(updatedLead.getLeadSource());
        existing.setStageId(updatedLead.getStageId());
        existing.setDesignStyle(updatedLead.getDesignStyle());
//        existing.setRole(updatedLead.getRole());
        existing.setAssignedSalesPerson(updatedLead.getAssignedSalesPerson());
        existing.setAssignedDesigner(updatedLead.getAssignedDesigner());
        existing.setInitialQuotedAmount(updatedLead.getInitialQuotedAmount());
        existing.setFinalQuotation(updatedLead.getFinalQuotation());
        existing.setSignUpAmount(updatedLead.getSignUpAmount());
        existing.setPaymentDate(updatedLead.getPaymentDate());
        existing.setPaymentMode(updatedLead.getPaymentMode());
        existing.setPanNumber(updatedLead.getPanNumber());
        existing.setProjectTimeline(updatedLead.getProjectTimeline());
        existing.setDiscount(updatedLead.getDiscount());
        existing.setPaymentDetailsFile(updatedLead.getPaymentDetailsFile());
        existing.setBookingFormFile(updatedLead.getBookingFormFile());
//        existing.setReasonForLoss(updatedLead.getReasonForLoss());
//        existing.setReasonForMarkingAsJunk(updatedLead.getReasonForMarkingAsJunk());
        existing.setPriority(updatedLead.getPriority());
        existing.setDateOfCreation(updatedLead.getDateOfCreation());
        // DO NOT overwrite activities, notes, or activityLog!
//        validateManagerFields(existing);
        return leadRepository.save(existing);
    }

    // -------------------- Lead Status Change & Logging --------------------
//    public ModelLead updateLeadStatusByStageId(String leadId, String stageId) {
//        // Fetch the lead by leadId or throw 404 if not found
//        ModelLead lead = leadRepository.findByLeadId(leadId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
//
//        // Fetch the pipeline stage by stageId or throw 400 if not found
//        PipelineStageModel newStage = pipelineStageRepository.findByStageId(stageId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stage not found: " + stageId));
//
//        // Store old stage info for logging
//        String oldStageName = lead.getStageName();
//        String oldStageId = lead.getStageId();
//
//        // Update lead's stage details
//        lead.setStageId(newStage.getStageId());
//        lead.setStageName(newStage.getStageName());
//        // No pipelineGroupId to set
//
//        // Set lead status to the new stage name
//        lead.setStageName(newStage.getStageName());
//
//        // Log the stage and status change in the activity log
//        addActivityLogEntry(
//                lead,
//                "lead",
//                oldStageName,                    // Old stage name
//                newStage.getStageName(),         // New stage name
//                "Stage changed from '" + oldStageName + "' to '" + newStage.getStageName() + "'",
//                lead.getRole() != null ? lead.getRole().name() : "User"
//        );
//
//        // Save and return the updated lead
//        return leadRepository.save(lead);
//    }
    public ModelLead updateLeadStatusByStageId(String leadId, String stageId) {
        // Fetch the lead by leadId or throw 404 if not found
        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));

        // Fetch the pipeline stage by stageId or throw 400 if not found
        PipelineStageModel newStage = pipelineStageRepository.findByStageId(stageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stage not found: " + stageId));

        // Store old stage info for logging
        String oldStageId = lead.getStageId();
        String oldStageName = lead.getStageName();

        // Update lead's stage details
        lead.setStageId(newStage.getStageId());
        lead.setStageName(newStage.getStageName());

        // Log the stage change in the activity log
        addActivityLogEntry(
                lead,
                "lead",
                oldStageId,                // previousStageId
                oldStageName,              // previousStageName
                newStage.getStageId(),     // newStageId
                newStage.getStageName(),   // newStageName
                "Stage changed from '" + oldStageName + "' to '" + newStage.getStageName() + "'"

        );

        // Save and return the updated lead
        return leadRepository.save(lead);
    }


    // -------------------- Notes Management --------------------
    public ModelLead.Note addNoteToLead(String leadId, String content) {
        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));

        ModelLead.Note note = new ModelLead.Note();
        note.setNoteId("NID" + snowflakeIdGenerator.nextId());
        note.setNote(content);
//        note.setUser(lead.getRole() != null ? lead.getRole().name() : "User");
        note.setTimestamp(LocalDateTime.now().toString());

        if (lead.getNotes() == null) {
            lead.setNotes(new ArrayList<>());
        }

        lead.getNotes().add(note);

        leadRepository.save(lead);
        return note;
    }

    public List<ModelLead.Note> getNotesForLead(String leadId) {
        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));

        return lead.getNotes() != null ? lead.getNotes() : new ArrayList<>();
    }

    // -------------------- Activity Management & Logging --------------------


public ModelLead.ActivityDetails addActivity(String leadId, ActivityDetailsDto dto) {
    ModelLead lead = leadRepository.findByLeadId(leadId)
            .orElseThrow(() -> new RuntimeException("Lead not found"));

    ModelLead.ActivityDetails activity = new ModelLead.ActivityDetails();
    activity.setActivityId("AID" + snowflakeIdGenerator.nextId());
    activity.setType(dto.getType());
    activity.setTitle(dto.getTitle());
    activity.setPurposeOfTheCall(dto.getPurposeOfTheCall());
    activity.setOutComeOfTheCall(dto.getOutComeOfTheCall());
    activity.setDueDate(dto.getDueDate());
    activity.setTime(dto.getTime());
    activity.setNextFollowUp(dto.getNextFollowUp());
    activity.setAssignedTo(dto.getAssignedTo());
    activity.setStatus(dto.getStatus());
    activity.setMeetingVenue(dto.getMeetingVenue());
    activity.setMeetingLink(dto.getMeetingLink());
    activity.setAttendees(dto.getAttendees());
    activity.setOutcomeOfTheMeeting(dto.getOutcomeOfTheMeeting());


//    MultipartFile attachFile = dto.getAttachFile();
//    if (attachFile != null && !attachFile.isEmpty()) {
//        String bucketName = minioService.getDocumentBucketName();
//        String attachUrl = minioService.uploadFile(bucketName, attachFile, leadId);
//        activity.setAttach(attachUrl);
//    }
    MultipartFile attachFile = dto.getAttach();
    if (attachFile != null && !attachFile.isEmpty()) {
        String bucketName = "lead";
        String attachUrl = minioService.uploadFile(bucketName, attachFile, leadId);
        activity.setAttach(attachUrl);
    }




    if (lead.getActivities() == null) {
        lead.setActivities(new ArrayList<>());
    }
    lead.getActivities().add(activity);

    // Add Activity Log Entry
    ModelLead.ActivityLogEntry logEntry = new ModelLead.ActivityLogEntry();
    logEntry.setLogId("LOG" + snowflakeIdGenerator.nextId());
    logEntry.setType(activity.getType());
//    logEntry.setSummary("Activity '" + activity.getTitle() + "' added by " + activity.getAssignedTo());
    String summary = activity.getTitle()  ;
    if (activity.getAttach() != null && !activity.getAttach().isEmpty()) {
        summary += "Attachment " + activity.getAttach();
    }
    logEntry.setSummary(summary);
    logEntry.setPerformedBy(activity.getAssignedTo());
    logEntry.setTimestamp(LocalDateTime.now().toString());
    lead.getActivityLog().add(logEntry);

    leadRepository.save(lead);
    return activity;
}


    public List<ModelLead.ActivityDetails> getAllActivities(String leadId) {
        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
        return lead.getActivities() != null ? lead.getActivities() : new ArrayList<>();
    }


public ModelLead.ActivityDetails updateActivity(String leadId, String activityId, ActivityDetailsDto dto) {
    ModelLead lead = leadRepository.findByLeadId(leadId)
            .orElseThrow(() -> new RuntimeException("Lead not found"));

    List<ModelLead.ActivityDetails> activities = lead.getActivities();
    if (activities == null) throw new RuntimeException("No activities found");

    ModelLead.ActivityDetails existing = null;
    int idx = -1;
    for (int i = 0; i < activities.size(); i++) {
        if (activities.get(i).getActivityId().equals(activityId)) {
            existing = activities.get(i);
            idx = i;
            break;
        }
    }
//    if (existing == null) throw new RuntimeException("Activity not found");

    // Update fields
    existing.setType(dto.getType());
    existing.setTitle(dto.getTitle());
    existing.setPurposeOfTheCall(dto.getPurposeOfTheCall());
    existing.setOutComeOfTheCall(dto.getOutComeOfTheCall());
    existing.setDueDate(dto.getDueDate());
    existing.setTime(dto.getTime());
    existing.setNextFollowUp(dto.getNextFollowUp());
    existing.setAssignedTo(dto.getAssignedTo());
    existing.setStatus(dto.getStatus());
    existing.setMeetingVenue(dto.getMeetingVenue());
    existing.setMeetingLink(dto.getMeetingLink());
    existing.setAttendees(dto.getAttendees());
    existing.setOutcomeOfTheMeeting(dto.getOutcomeOfTheMeeting());

    MultipartFile attachFile = dto.getAttach();
    if (attachFile != null && !attachFile.isEmpty()) {
        String bucketName = "lead";
        String attachUrl = minioService.uploadFile(bucketName, attachFile, leadId);
        existing.setAttach(attachUrl);
    }

    activities.set(idx, existing);

    // Add Activity Log Entry
    String status = existing.getStatus();
    String logSummary;
    if ("pending".equalsIgnoreCase(status) || "done".equalsIgnoreCase(status) || "delete".equalsIgnoreCase(status)) {
        logSummary = existing.getType() + existing.getTitle() + "' marked as " + status;
    } else {
        logSummary = "Activity '" + existing.getTitle() + "' updated by " + existing.getAssignedTo();
    }

    ModelLead.ActivityLogEntry logEntry = new ModelLead.ActivityLogEntry();
    logEntry.setLogId("LOG" + snowflakeIdGenerator.nextId());
    logEntry.setType(existing.getType()); // e.g., To-Do, Email, etc.
    logEntry.setTitle(existing.getTitle());
    logEntry.setSummary(logSummary);
    logEntry.setPerformedBy(existing.getAssignedTo());
    logEntry.setTimestamp(LocalDateTime.now().toString());
    lead.getActivityLog().add(logEntry);

    leadRepository.save(lead);
    return existing;
}


    //    public void deleteActivity(String leadId, String activityId) {
//        ModelLead lead = leadRepository.findByLeadId(leadId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
//        if (lead.getActivities() != null) {
//            lead.getActivities().removeIf(a -> activityId.equals(a.getActivityId()));
//            leadRepository.save(lead);
//        }
//    }
public void deleteActivity(String leadId, String activityId) {
    ModelLead lead = leadRepository.findByLeadId(leadId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));

    if (lead.getActivities() != null) {
        Iterator<ModelLead.ActivityDetails> iterator = lead.getActivities().iterator();
        while (iterator.hasNext()) {
            ModelLead.ActivityDetails activity = iterator.next();
            if (activityId.equals(activity.getActivityId())) {
                // Log the deletion in the activity log
                addActivityLogEntry(
                        lead,
                        "activity",
                        null, null, null, null,
                        "Activity deleted: '" + activity.getType() + "'"
//                        lead.getRole() != null ? lead.getRole().name() : "SYSTEM"
                );
                iterator.remove();
                break;
            }
        }
        leadRepository.save(lead);
    }
}

    // -------------------- Activity Log Helper --------------------

    private void addActivityLogEntry(ModelLead lead, String type, String previousStageId, String previousStageName,
                                     String newStageId, String newStageName,
                                     String summary) {
        // 1. Get existing logs or initialize if null
        List<ModelLead.ActivityLogEntry> logs = lead.getActivityLog();
        if (logs == null) {
            logs = new ArrayList<>();
            lead.setActivityLog(logs); // Attach to lead if new
        }

        // 2. Create new log entry
        ModelLead.ActivityLogEntry logEntry = new ModelLead.ActivityLogEntry();
        logEntry.setLogId("LOG" + snowflakeIdGenerator.nextId());
        logEntry.setType(type);
        logEntry.setPreviousStageId(previousStageId);
        logEntry.setPreviousStageName(previousStageName);
        logEntry.setNewStageId(newStageId);
        logEntry.setNewStageName(newStageName);
        logEntry.setSummary(summary);
//        logEntry.setTitle(title);

        logEntry.setTimestamp(LocalDateTime.now().toString());

        // 3. Append to existing logs (not replace)
        logs.add(logEntry);
    }

    public List<ModelLead.ActivityLogEntry> getActivityLogForLead(String leadId) {
        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
        List<ModelLead.ActivityLogEntry> log = lead.getActivityLog();
        return log != null ? log : new ArrayList<>();
    }

    // -------------------- Validation --------------------
//    private void validateManagerFields(ModelLead lead) {
//        if (lead.getRole() != null && lead.getRole().name().equalsIgnoreCase("MANAGER")) {
//            if (isEmpty(lead.getAssignedSalesPerson()) || isEmpty(lead.getAssignedDesigner())) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                        "Manager must have both assignedSalesPerson and assignedDesigner.");
//            }
//        }
//    }

    private boolean isEmpty(String val) {
        return val == null || val.trim().isEmpty();
    }

}
