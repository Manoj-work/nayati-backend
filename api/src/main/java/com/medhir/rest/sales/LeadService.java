package com.medhir.rest.sales;

import com.medhir.rest.repository.EmployeeRepository;
import com.medhir.rest.sales.dto.ActivityDetailsDto;
import com.medhir.rest.utils.MinioService;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.medhir.rest.service.EmployeeService;
import com.medhir.rest.model.EmployeeModel;
import com.medhir.rest.sales.dto.LeadResponseDTO;

import java.util.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
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

    @Autowired
    private PipelineStageService stageService;


public ModelLead createLead(ModelLead lead) {

//    if (allStages.isEmpty()) {
//        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No stages found. Please create stages before proceeding.");
//    }
    PipelineStageModel stage = pipelineStageRepository.findByStageNameIgnoreCase("New")
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Default stage 'New' not found"));

    // Set stage details on the lead
    lead.setStageId(stage.getStageId());
    lead.setStageName(stage.getStageName());
    // Validate assignedSalesPerson by employeeId field
    if (lead.getAssignedSalesPerson() != null &&
            !employeeRepository.existsByEmployeeId(lead.getAssignedSalesPerson())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Assigned Sales Person ID does not exist: " + lead.getAssignedSalesPerson());
    }

    // Validate assignedDesigner by employeeId field
    if (lead.getAssignedDesigner() != null &&
            !employeeRepository.existsByEmployeeId(lead.getAssignedDesigner())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Assigned Designer ID does not exist: " + lead.getAssignedDesigner());
    }

    // Validate createdBy by employeeId field
    if (lead.getCreatedBy() != null &&
            !employeeRepository.existsByEmployeeId(lead.getCreatedBy())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Created By ID does not exist: " + lead.getCreatedBy());
    }

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

    public List<ModelLead> getAllLeads() {
        return leadRepository.findAll();
    }


    public Optional<ModelLead> getLeadByLeadId(String leadId) {
        return leadRepository.findByLeadId(leadId);
    }

    //--------------Find Lead by Employee ID

    public List<ModelLead> getLeadsByEmployeeId(String employeeId) {
        boolean employeeExists = employeeRepository.existsByEmployeeId(employeeId);
        if (!employeeExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee ID not found: " + employeeId);
        }

        List<ModelLead> leads = leadRepository.findByAssignedSalesPersonOrAssignedDesignerOrCreatedBy(employeeId, employeeId, employeeId);

        if (leads.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No leads found for employee ID: " + employeeId);
        }

        return leads;
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
        if (updatedLead.getStageId() != null) {
            existing.setStageId(updatedLead.getStageId());
        }
        existing.setStageName(updatedLead.getStageName());
        existing.setDesignStyle(updatedLead.getDesignStyle());
        existing.setArea(updatedLead.getArea());
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
//    logEntry.setPerformedBy(activity.getAssignedTo());
    logEntry.setTimestamp(LocalDateTime.now().toString());
    lead.getActivityLog().add(logEntry);

    leadRepository.save(lead);
    return activity;
}



    // ADD THIS NEW METHOD FOR BULK ACTIVITIES WITH FILES

    public List<ModelLead.ActivityDetails> addActivitiesWithFilesAndAttachments(
            String leadId,
            List<ActivityDetailsDto> activitiesDto,
            MultipartFile callAttachment,
            MultipartFile todoAttachment,
            MultipartFile meetingAttachment,
            MultipartFile emailAttachment) {

        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));

        if (lead.getActivities() == null) {
            lead.setActivities(new ArrayList<>());
        }
        if (lead.getActivityLog() == null) {
            lead.setActivityLog(new ArrayList<>());
        }

        List<ModelLead.ActivityDetails> createdActivities = new ArrayList<>();
        String bucketName = "lead";

        for (ActivityDetailsDto dto : activitiesDto) {
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


            String attachUrl = null;

            switch (dto.getType().toLowerCase()) {
                case "call":
                    if (callAttachment != null && !callAttachment.isEmpty()) {
                        attachUrl = minioService.uploadFile(bucketName, callAttachment, leadId);
                        activity.setCallAttachmentUrl(attachUrl);
                    }
                    break;

                case "to-do":
                    if (todoAttachment != null && !todoAttachment.isEmpty()) {
                        attachUrl = minioService.uploadFile(bucketName, todoAttachment, leadId);
                        activity.setTodoAttachmentUrl(attachUrl);
                    }
                    break;

                case "meeting":
                    if (meetingAttachment != null && !meetingAttachment.isEmpty()) {
                        attachUrl = minioService.uploadFile(bucketName, meetingAttachment, leadId);
                        activity.setMeetingAttachmentUrl(attachUrl);  // Corrected setter
                    }
                    break;

                case "email":
                    if (emailAttachment != null && !emailAttachment.isEmpty()) {
                        attachUrl = minioService.uploadFile(bucketName, emailAttachment, leadId);
                        activity.setEmailAttachmentUrl(attachUrl);  // Corrected setter
                    }
                    break;

                default:
                    // No attachment or unknown type
                    break;
            }




            lead.getActivities().add(activity);
            createdActivities.add(activity);

            // Add activity log entry
            ModelLead.ActivityLogEntry logEntry = new ModelLead.ActivityLogEntry();
            logEntry.setLogId("LOG" + snowflakeIdGenerator.nextId());
            logEntry.setType("Activity Added");
            String logSummary = "Activity '" + activity.getTitle() + "' (" + activity.getType() + ") added by " + activity.getAssignedTo();
            if (activity.getAttach() != null && !activity.getAttach().isEmpty()) {
                logSummary += ". Attached file: " + activity.getAttach();
            }
            logEntry.setSummary(logSummary);
//            logEntry.setPerformedBy(activity.getAssignedTo());
            logEntry.setTimestamp(LocalDateTime.now().toString());
            lead.getActivityLog().add(logEntry);
        }

        leadRepository.save(lead);
        return createdActivities;

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
//    logEntry.setPerformedBy(existing.getAssignedTo());
    logEntry.setTimestamp(LocalDateTime.now().toString());
    lead.getActivityLog().add(logEntry);

    leadRepository.save(lead);
    return existing;
}


    public ModelLead.ActivityDetails updateActivityWithTypeAttachment(
            String leadId, String activityId, ActivityDetailsDto dto,
            MultipartFile callAttachment, MultipartFile todoAttachment,
            MultipartFile meetingAttachment, MultipartFile emailAttachment
    ) {
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
        if (existing == null) throw new RuntimeException("Activity not found");

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

        // Map activity type to corresponding file attachment
        MultipartFile attachFile = null;
        if (dto.getType() != null) {
            switch (dto.getType().toLowerCase()) {
                case "call":
                    attachFile = callAttachment;
                    break;
                case "to-do":
                case "todo":
                    attachFile = todoAttachment;
                    break;
                case "meeting":
                    attachFile = meetingAttachment;
                    break;
                case "email":
                    attachFile = emailAttachment;
                    break;
                default:
                    // No attachment or unknown type
                    break;
            }
        }

        if (attachFile != null && !attachFile.isEmpty()) {
            String bucketName = "lead";
            String attachUrl = minioService.uploadFile(bucketName, attachFile, leadId);
            existing.setAttach(attachUrl);
        }

        activities.set(idx, existing);

        // Add Activity Log Entry
        if (lead.getActivityLog() == null) {
            lead.setActivityLog(new java.util.ArrayList<>());
        }
        String status = existing.getStatus();
        String logSummary;
        if ("pending".equalsIgnoreCase(status) || "done".equalsIgnoreCase(status) || "delete".equalsIgnoreCase(status)) {
            logSummary = existing.getType() + " " + existing.getTitle() + " marked as " + status;
        } else {
            logSummary = "Activity '" + existing.getTitle() + "' updated by " + existing.getAssignedTo();
        }

        ModelLead.ActivityLogEntry logEntry = new ModelLead.ActivityLogEntry();
        logEntry.setLogId("LOG" + snowflakeIdGenerator.nextId());
        logEntry.setType(existing.getType());
        logEntry.setTitle(existing.getTitle());
        logEntry.setSummary(logSummary);
//        logEntry.setPerformedBy(existing.getAssignedTo());
        logEntry.setTimestamp(LocalDateTime.now().toString());
        lead.getActivityLog().add(logEntry);

        leadRepository.save(lead);
        return existing;
    }


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


    private boolean isEmpty(String val) {
        return val == null || val.trim().isEmpty();
    }


    public LeadResponseDTO toLeadResponseDTO(ModelLead lead) {
        Set<String> employeeIds = new HashSet<>();
        if (lead.getCreatedBy() != null) employeeIds.add(lead.getCreatedBy());
        if (lead.getAssignedSalesPerson() != null) employeeIds.add(lead.getAssignedSalesPerson());
        if (lead.getAssignedDesigner() != null) employeeIds.add(lead.getAssignedDesigner());

        List<EmployeeModel> employees = employeeRepository.findByEmployeeIdIn(employeeIds);
        Map<String, EmployeeModel> employeeMap = employees.stream()
                .collect(Collectors.toMap(EmployeeModel::getEmployeeId, e -> e));

        return mapLeadToDTO(lead, employeeMap);
    }

    public List<LeadResponseDTO> toLeadResponseDTOList(List<ModelLead> leads) {
        Set<String> employeeIds = leads.stream()
                .flatMap(lead -> Stream.of(lead.getCreatedBy(), lead.getAssignedSalesPerson(), lead.getAssignedDesigner()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<EmployeeModel> employees = employeeRepository.findByEmployeeIdIn(employeeIds);
        Map<String, EmployeeModel> employeeMap = employees.stream()
                .collect(Collectors.toMap(EmployeeModel::getEmployeeId, e -> e));

        return leads.stream().map(lead -> mapLeadToDTO(lead, employeeMap)).collect(Collectors.toList());
    }

    private LeadResponseDTO mapLeadToDTO(ModelLead lead, Map<String, EmployeeModel> employeeMap) {
        LeadResponseDTO dto = new LeadResponseDTO();
        dto.setLeadId(lead.getLeadId());
        dto.setName(lead.getName());
        dto.setEmail(lead.getEmail());
        dto.setContactNumber(lead.getContactNumber());
        dto.setProjectType(lead.getProjectType());
        dto.setPropertyType(lead.getPropertyType());
        dto.setAddress(lead.getAddress());
        dto.setBudget(lead.getBudget());
        dto.setLeadSource(lead.getLeadSource());
        dto.setDesignStyle(lead.getDesignStyle());
        dto.setPriority(lead.getPriority());
        dto.setDateOfCreation(lead.getDateOfCreation());
        dto.setStageId(lead.getStageId());
        dto.setStageName(lead.getStageName());
        dto.setInitialQuotedAmount(lead.getInitialQuotedAmount());
        dto.setFinalQuotation(lead.getFinalQuotation());
        dto.setSignUpAmount(lead.getSignUpAmount());
        dto.setPaymentDate(lead.getPaymentDate());
        dto.setPaymentMode(lead.getPaymentMode());
        dto.setPanNumber(lead.getPanNumber());
        dto.setProjectTimeline(lead.getProjectTimeline());
        dto.setDiscount(lead.getDiscount());
        dto.setPaymentDetailsFile(lead.getPaymentDetailsFile());
        dto.setBookingFormFile(lead.getBookingFormFile());
        dto.setReasonForLoss(lead.getReasonForLoss());
        dto.setReasonForMarkingAsJunk(lead.getReasonForMarkingAsJunk());

        // Employee info
        EmployeeModel creator = employeeMap.get(lead.getCreatedBy());
        dto.setCreatedById(lead.getCreatedBy());
        dto.setCreatedByName(creator != null ? creator.getName() : null);

        EmployeeModel sales = employeeMap.get(lead.getAssignedSalesPerson());
        dto.setAssignedSalesPersonId(lead.getAssignedSalesPerson());
        dto.setAssignedSalesPersonName(sales != null ? sales.getName() : null);

        EmployeeModel designer = employeeMap.get(lead.getAssignedDesigner());
        dto.setAssignedDesignerId(lead.getAssignedDesigner());
        dto.setAssignedDesignerName(designer != null ? designer.getName() : null);

        // Activities
        if (lead.getActivities() != null) {
            dto.setActivities(
                    lead.getActivities().stream().map(activity -> {
                        LeadResponseDTO.ActivityDetailsDTO adto = new LeadResponseDTO.ActivityDetailsDTO();
                        adto.setActivityId(activity.getActivityId());
                        adto.setType(activity.getType());
                        adto.setTitle(activity.getTitle());
                        adto.setPurposeOfTheCall(activity.getPurposeOfTheCall());
                        adto.setOutComeOfTheCall(activity.getOutComeOfTheCall());
                        adto.setDueDate(activity.getDueDate());
                        adto.setTime(activity.getTime());
                        adto.setNextFollowUp(activity.getNextFollowUp());
                        adto.setAssignedTo(activity.getAssignedTo());
                        adto.setStatus(activity.getStatus());
                        adto.setMeetingVenue(activity.getMeetingVenue());
                        adto.setMeetingLink(activity.getMeetingLink());
                        adto.setOutcomeOfTheMeeting(activity.getOutcomeOfTheMeeting());
                        adto.setAttendees(activity.getAttendees());
                        adto.setAttach(activity.getAttach());
                        return adto;
                    }).collect(Collectors.toList())
            );
        }

        // Notes
        if (lead.getNotes() != null) {
            dto.setNotes(
                    lead.getNotes().stream().map(note -> {
                        LeadResponseDTO.NoteDTO ndto = new LeadResponseDTO.NoteDTO();
                        ndto.setNoteId(note.getNoteId());
                        ndto.setNote(note.getNote());
                        ndto.setUser(note.getUser());
                        ndto.setTimestamp(note.getTimestamp());
                        return ndto;
                    }).collect(Collectors.toList())
            );
        }

        // Activity Log
        if (lead.getActivityLog() != null) {
            dto.setActivityLog(
                    lead.getActivityLog().stream().map(log -> {
                        LeadResponseDTO.ActivityLogEntryDTO ldto = new LeadResponseDTO.ActivityLogEntryDTO();
                        ldto.setLogId(log.getLogId());
                        ldto.setType(log.getType());
                        ldto.setPreviousStageId(log.getPreviousStageId());
                        ldto.setPreviousStageName(log.getPreviousStageName());
                        ldto.setNewStageId(log.getNewStageId());
                        ldto.setNewStageName(log.getNewStageName());
                        ldto.setSummary(log.getSummary());
                        ldto.setTitle(log.getTitle());
//                        ldto.setPerformedBy(log.getPerformedBy());
                        ldto.setTimestamp(log.getTimestamp());
                        return ldto;
                    }).collect(Collectors.toList())
            );
        }

        return dto;
    }

}
