package com.medhir.rest.sales.service;

import com.medhir.rest.sales.model.LeadModel;
import com.medhir.rest.sales.model.Activity;
import com.medhir.rest.sales.model.ActivityLog;
import com.medhir.rest.sales.model.Note;
import com.medhir.rest.sales.repository.LeadRepository;
import com.medhir.rest.sales.repository.KanbanLeadProjection;
import com.medhir.rest.sales.dto.lead.ConvertLeadRequestDTO;
import com.medhir.rest.sales.dto.lead.LeadAssignmentRequestDTO;
import com.medhir.rest.sales.dto.lead.LeadRequestDTO;
import com.medhir.rest.sales.dto.activity.ActivityLogRequestDTO;
import com.medhir.rest.sales.dto.lead.LeadConversionResponseDTO;
import com.medhir.rest.sales.dto.activity.ActivityDTO;
import com.medhir.rest.sales.dto.activity.NoteDTO;
import com.medhir.rest.testModuleforsales.Customer;
import com.medhir.rest.testModuleforsales.CustomerRepository;
import com.medhir.rest.utils.GeneratedId;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import com.medhir.rest.utils.MinioService;
import com.medhir.rest.service.EmployeeService;
import com.medhir.rest.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.medhir.rest.sales.dto.lead.LeadProjectCustomerResponseDTO;

@Service
public class LeadService {

    @Autowired
    private CustomerRepository customerRepository;


    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    private MinioService minioService;

    @Autowired
    private PipelineStageService pipelineStageService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private GeneratedId generatedId;

    // 🔍 Fetch all leads
    public List<LeadModel> getAllLeads() {
        return leadRepository.findAll();
    }

    // �� Get single lead by leadId (Snowflake ID)
    public LeadModel getLeadByLeadId(String leadId) {
        return leadRepository.findByLeadId(leadId)

                .orElseThrow(() -> new RuntimeException(" Lead not found with leadId: " + leadId));

    }

    // Helper: Validate stageId exists
    private void validateStageId(String stageId) {
        if (!pipelineStageService.stageExistsById(stageId)) {

            throw new RuntimeException(" Invalid stageId: " + stageId + ". Please use a valid pipeline stage ID.");

        }
    }

    // Helper: Assign fields from LeadRequestDTO to LeadModel
    private void assignLeadFields(LeadModel lead, LeadRequestDTO dto) {
        if (dto.getName() != null) lead.setName(dto.getName());
        if (dto.getContactNumber() != null) lead.setContactNumber(dto.getContactNumber());
        if (dto.getEmail() != null) lead.setEmail(dto.getEmail());
        if (dto.getProjectType() != null) lead.setProjectType(dto.getProjectType());
        if (dto.getPropertyType() != null) lead.setPropertyType(dto.getPropertyType());
        if (dto.getAddress() != null) lead.setAddress(dto.getAddress());
        if (dto.getArea() != null) lead.setArea(dto.getArea());
        if (dto.getBudget() != null) lead.setBudget(dto.getBudget());
        if (dto.getDesignStyle() != null) lead.setDesignStyle(dto.getDesignStyle());
        if (dto.getLeadSource() != null) lead.setLeadSource(dto.getLeadSource());
        if (dto.getNotes() != null) lead.setNotes(dto.getNotes());
        if (dto.getStageId() != null) lead.setStageId(dto.getStageId());
        if (dto.getPriority() != null) lead.setPriority(dto.getPriority());
        if (dto.getSalesRep() != null) {
            if (!employeeService.getEmployeeById(dto.getSalesRep()).isPresent()) {
                throw new RuntimeException("SalesRep with ID " + dto.getSalesRep() + " does not exist.");
            }
            lead.setSalesRep(dto.getSalesRep());
        }
        if (dto.getDesigner() != null) {
            if (!employeeService.getEmployeeById(dto.getDesigner()).isPresent()) {
                throw new RuntimeException("Designer with ID " + dto.getDesigner() + " does not exist.");
            }
            lead.setDesigner(dto.getDesigner());
        }
        if (dto.getQuotedAmount() != null) lead.setQuotedAmount(dto.getQuotedAmount());
        if (dto.getFinalQuotation() != null) lead.setFinalQuotation(dto.getFinalQuotation());
        if (dto.getSignupAmount() != null) lead.setSignupAmount(dto.getSignupAmount());
        if (dto.getPaymentDate() != null) lead.setPaymentDate(dto.getPaymentDate());
        if (dto.getPaymentMode() != null) lead.setPaymentMode(dto.getPaymentMode());
        if (dto.getPanNumber() != null) lead.setPanNumber(dto.getPanNumber());
        if (dto.getDiscount() != null) lead.setDiscount(dto.getDiscount());
        if (dto.getReasonForLost() != null) lead.setReasonForLost(dto.getReasonForLost());
        if (dto.getReasonForJunk() != null) lead.setReasonForJunk(dto.getReasonForJunk());
        if (dto.getSubmittedBy() != null) lead.setSubmittedBy(dto.getSubmittedBy());
        if (dto.getPaymentDetailsFileName() != null) lead.setPaymentDetailsFileName(dto.getPaymentDetailsFileName());
        if (dto.getBookingFormFileName() != null) lead.setBookingFormFileName(dto.getBookingFormFileName());
        if (dto.getInitialQuote() != null) lead.setInitialQuote(dto.getInitialQuote());
        if (dto.getProjectTimeline() != null) lead.setProjectTimeline(dto.getProjectTimeline());
        if (dto.getAssignedSalesRep() != null) lead.setAssignedSalesRep(dto.getAssignedSalesRep());
        if (dto.getAssignedDesigner() != null) lead.setAssignedDesigner(dto.getAssignedDesigner());
        if (dto.getDateOfCreation() != null) lead.setDateOfCreation(dto.getDateOfCreation());
    }

    // Helper: Set default stageId if not provided
    private void setDefaultStageIdIfMissing(LeadModel lead) {
        if (lead.getStageId() == null || lead.getStageId().trim().isEmpty()) {
            List<String> stageNames = pipelineStageService.getStageNames();
            if (!stageNames.isEmpty()) {
                String defaultStageName = stageNames.get(0); // "New"
                var defaultStage = pipelineStageService.getStageByName(defaultStageName);
                if (defaultStage.isPresent()) {
                    lead.setStageId(defaultStage.get().getStageId());
                }
            }
        }
    }

    // Helper: Create a new LeadModel from LeadRequestDTO
    private LeadModel createLeadModelFromDTO(LeadRequestDTO dto) {
        LeadModel lead = new LeadModel();
        assignLeadFields(lead, dto);
        return lead;
    }

    //  Create a new lead
    public LeadModel createLead(LeadModel lead) {
        if (lead.getLeadId() == null || lead.getLeadId().isBlank()) {
            lead.setLeadId("LEAD-" + snowflakeIdGenerator.nextId());
        }
        if (lead.getCustomerId() == null || lead.getCustomerId().isBlank()) {
            lead.setCustomerId("CUS-" + snowflakeIdGenerator.nextId());
        }
        lead.setGeneratedId(generatedId);  // Injected service or bean
        lead.generateProjectName();

        Customer customer = Customer.builder()
                .customerId(lead.getCustomerId())
                .customerName(lead.getName())
                .email(lead.getEmail())
                .contactNumber(lead.getContactNumber())
                .build();

        customerRepository.save(customer);

        setDefaultStageIdIfMissing(lead);
        validateStageId(lead.getStageId());
        return leadRepository.save(lead);
    }

    // Overload: Create a new lead from DTO
    public LeadModel createLead(LeadRequestDTO dto) {
        LeadModel lead = createLeadModelFromDTO(dto);
//        if (lead.getLeadId() == null || lead.getCustomerId().isBlank()) {
//            lead.setLeadId("CUS" + snowflakeIdGenerator.nextId());
//        }
        return createLead(lead);
    }

    // ✏️ Update existing lead
    public LeadModel updateLead(String id, LeadModel updatedLead) {
        LeadModel existingLead = getLeadByLeadId(id); // Ensures lead exists
        updatedLead.setLeadId(existingLead.getLeadId()); // Retain original ID
        if (updatedLead.getStageId() != null && !updatedLead.getStageId().trim().isEmpty()) {
            validateStageId(updatedLead.getStageId());
        }
        return leadRepository.save(updatedLead);
    }

    // Overload: Update lead from DTO
    public LeadModel updateLead(String id, LeadRequestDTO dto) {
        LeadModel existingLead = getLeadByLeadId(id);
        assignLeadFields(existingLead, dto);
        if (existingLead.getStageId() != null && !existingLead.getStageId().trim().isEmpty()) {
            validateStageId(existingLead.getStageId());
        }
        return leadRepository.save(existingLead);
    }

    //  Delete lead
    public void deleteLead(String id) {
        if (!leadRepository.existsById(id)) {
            throw new RuntimeException(" Cannot delete. Lead not found with id: " + id);
        }
        leadRepository.deleteById(id);
    }

    // Get leads by stageId (changed from status for better data integrity)
    public List<LeadModel> getLeadsByStageId(String stageId) {
        return leadRepository.findByStageId(stageId);
    }

    // Update lead stageId (changed from status for better data integrity)
    public LeadModel updateLeadStageId(String leadId, String stageId) {
        LeadModel lead = getLeadByLeadId(leadId);
        String oldStageId = lead.getStageId();
        
        // Validate that the new stageId exists in pipeline stages
        validateStageId(stageId);
        
        lead.setStageId(stageId);
        
        // Log stage change
        logStageChange(lead, oldStageId, stageId, lead.getSubmittedBy() != null ? lead.getSubmittedBy() : "System");
        
        return leadRepository.save(lead);
    }

    // 🎯 Activity Logging Methods - Only Stage Changes and Activity Completion
    public void logStageChange(LeadModel lead, String oldStageId, String newStageId, String user) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("oldStageId", oldStageId);
        metadata.put("newStageId", newStageId);
        
        // Get stage names for better logging
        String oldStageName = "Unknown";
        String newStageName = "Unknown";
        
        var oldStage = pipelineStageService.getStageByIdOptional(oldStageId);
        var newStage = pipelineStageService.getStageByIdOptional(newStageId);
        
        if (oldStage.isPresent()) {
            oldStageName = oldStage.get().getName();
        }
        if (newStage.isPresent()) {
            newStageName = newStage.get().getName();
        }
        
        ActivityLog log = new ActivityLog();
        log.setId("LOG-" + snowflakeIdGenerator.nextId());
        log.setAction("Stage changed");
        log.setDetails(oldStageName + " → " + newStageName);
        log.setUser(user);
        log.setTimestamp(LocalDateTime.now().toString());
        log.setActivityType("STATUS_CHANGE");
        log.setMetadata(metadata);
        
        if (lead.getActivityLogs() == null) {
            lead.setActivityLogs(new ArrayList<>());
        }
        lead.getActivityLogs().add(log);
    }

    public void logActivityCompletion(LeadModel lead, Activity activity, String user) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("activityId", activity.getId());
        metadata.put("activityType", activity.getType());
        metadata.put("activityTitle", activity.getTitle());
        metadata.put("activityNotes", activity.getNotes());
        
        ActivityLog log = new ActivityLog();
        log.setId("LOG-" + snowflakeIdGenerator.nextId());
        log.setAction("Activity completed");
        log.setDetails(activity.getNotes());
        log.setUser(user);
        log.setTimestamp(LocalDateTime.now().toString());
        log.setActivityType("ACTIVITY_COMPLETION");
        log.setMetadata(metadata);
        
        if (lead.getActivityLogs() == null) {
            lead.setActivityLogs(new ArrayList<>());
        }
        lead.getActivityLogs().add(log);
    }

    public List<LeadModel> getLeadsForSales(String employeeId) {
        return leadRepository.findBySalesRep(employeeId);
    }

    public List<LeadModel> getLeadsForSalesByStageId(String employeeId, String stageId) {
        return leadRepository.findBySalesRepAndStageId(employeeId, stageId);
    }

    // If you have a manager/team field, implement filtering by team/department here
    public List<LeadModel> getLeadsForManager(String managerId) {
        // For now, return all leads (or filter by team if you have that info)
        return leadRepository.findAll();
        // If you have a manager/team field:
        // return leadRepository.findByManagerId(managerId);
    }

    // 🎯 Lead Conversion Method
    public LeadModel convertLead(String leadId, ConvertLeadRequestDTO conversionData, String user) {
        LeadModel lead = getLeadByLeadId(leadId);
        
        // Get the "Converted" stage ID
        var convertedStage = pipelineStageService.getStageByName("Converted");
        if (convertedStage.isEmpty()) {

            throw new RuntimeException(" 'Converted' stage not found in pipeline");

        }
        String convertedStageId = convertedStage.get().getStageId();
        
        // Validate lead can be converted
        if (convertedStageId.equals(lead.getStageId())) {

            throw new RuntimeException(" Lead is already converted");

        }
        
        var lostStage = pipelineStageService.getStageByName("Lost");
        if (lostStage.isPresent() && lostStage.get().getStageId().equals(lead.getStageId())) {

            throw new RuntimeException(" Cannot convert a lost lead");

        }
        
        // Validate required lead information
        if (lead.getName() == null || lead.getName().trim().isEmpty()) {

            throw new RuntimeException(" Lead name is required for conversion");
        }
        
        if (lead.getContactNumber() == null || lead.getContactNumber().trim().isEmpty()) {
            throw new RuntimeException(" Contact number is required for conversion");
        }
        
        if (lead.getEmail() == null || lead.getEmail().trim().isEmpty()) {
            throw new RuntimeException(" Email is required for conversion");

        }
        
        // Validate conversion data
        if (conversionData.getFinalQuotation() == null || conversionData.getFinalQuotation().trim().isEmpty()) {

            throw new RuntimeException(" Final quotation is required for conversion");

        }
        
        if (conversionData.getSignupAmount() == null || conversionData.getSignupAmount().trim().isEmpty()) {
            throw new RuntimeException("Sign-up amount is required for conversion");
        }
        
        // Update lead with conversion data
        lead.setFinalQuotation(conversionData.getFinalQuotation());
        lead.setSignupAmount(conversionData.getSignupAmount());
        lead.setPaymentDate(conversionData.getPaymentDate());
        lead.setPaymentMode(conversionData.getPaymentMode());
        lead.setPanNumber(conversionData.getPanNumber());
        lead.setDiscount(conversionData.getDiscount());
        lead.setInitialQuote(conversionData.getInitialQuote());
        lead.setProjectTimeline(conversionData.getProjectTimeline());
        
        // Change stageId to CONVERTED
        lead.setStageId(convertedStageId);
        
        // Add conversion note
        if (lead.getNotesList() == null) {
            lead.setNotesList(new ArrayList<>());
        }
        
        String conversionNoteId = "NOTE-" + snowflakeIdGenerator.nextId();
        Note conversionNote = new Note();
        conversionNote.setId(conversionNoteId);
        conversionNote.setContent(" LEAD CONVERTED: " + conversionData.getConversionNotes());
        conversionNote.setUser(user);
        conversionNote.setTimestamp(LocalDateTime.now().toString());
        lead.getNotesList().add(conversionNote);
        
        return leadRepository.save(lead);
    }

    // 🎯 Lead Assignment Update Method
    public LeadModel updateLeadAssignment(String leadId, LeadAssignmentRequestDTO assignmentData, String manager) {
        LeadModel lead = getLeadByLeadId(leadId);
        // Validate salesRep
        String salesRepId = assignmentData.getAssignedSalesRep();
        if (salesRepId != null && !employeeService.getEmployeeById(salesRepId).isPresent()) {
            throw new RuntimeException("SalesRep with ID " + salesRepId + " does not exist.");
        }
        // Validate designer
        String designerId = assignmentData.getAssignedDesigner();
        if (designerId != null && !employeeService.getEmployeeById(designerId).isPresent()) {
            throw new RuntimeException("Designer with ID " + designerId + " does not exist.");
        }
        lead.setSalesRep(salesRepId);
        lead.setDesigner(designerId);
        return leadRepository.save(lead);
    }

    // 🎯 Activity Management Methods
    public LeadModel addActivity(String leadId, ActivityDTO activityDTO, String user) {
        LeadModel lead = getLeadByLeadId(leadId);
        
        if (lead.getActivities() == null) {
            lead.setActivities(new ArrayList<>());
        }
        
        Activity activity = new Activity();
        activity.setId("ACT-" + snowflakeIdGenerator.nextId());
        activity.setType(activityDTO.getType());
        activity.setTitle(activityDTO.getTitle());
        activity.setNotes(activityDTO.getNotes());
        activity.setDueDate(activityDTO.getDueDate());
        activity.setDueTime(activityDTO.getDueTime());
        activity.setUser(user);
        activity.setStatus(activityDTO.getStatus());
        activity.setMeetingLink(activityDTO.getMeetingLink());
        activity.setAttendees(activityDTO.getAttendees());
        activity.setCallPurpose(activityDTO.getCallPurpose());
        activity.setCallOutcome(activityDTO.getCallOutcome());
        activity.setNextFollowUpDate(activityDTO.getNextFollowUpDate());
        activity.setNextFollowUpTime(activityDTO.getNextFollowUpTime());
        activity.setMeetingVenue(activityDTO.getMeetingVenue());
        activity.setNote(activityDTO.getNote());
        activity.setAttachment(activityDTO.getAttachment());
        
        lead.getActivities().add(activity);
        // Log activity creation
        if (lead.getActivityLogs() == null) {
            lead.setActivityLogs(new ArrayList<>());
        }
        ActivityLog log = new ActivityLog();
        log.setId("LOG-" + snowflakeIdGenerator.nextId());
        log.setAction("Activity created");
        log.setDetails(activity.getTitle());
        log.setUser(user);
        log.setTimestamp(LocalDateTime.now().toString());
        log.setActivityType(activity.getType());
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("activityId", activity.getId());
        metadata.put("activityTitle", activity.getTitle());
        metadata.put("activityNotes", activity.getNotes());
        metadata.put("activityType", activity.getType());
        log.setMetadata(metadata);
        lead.getActivityLogs().add(log);
        
        return leadRepository.save(lead);
    }

    public LeadModel updateActivity(String leadId, String activityId, ActivityDTO activityDTO, String user) {
        LeadModel lead = getLeadByLeadId(leadId);
        
        if (lead.getActivities() == null) {
            throw new RuntimeException(" No activities found for this lead");
        }
        
        Activity activityToUpdate = lead.getActivities().stream()
                .filter(activity -> activity.getId().equals(activityId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(" Activity not found with id: " + activityId));
        
        // Update activity fields
        if (activityDTO.getType() != null) {
            activityToUpdate.setType(activityDTO.getType());
        }
        if (activityDTO.getTitle() != null) {
            activityToUpdate.setTitle(activityDTO.getTitle());
        }
        if (activityDTO.getNotes() != null) {
            activityToUpdate.setNotes(activityDTO.getNotes());
        }
        if (activityDTO.getDueDate() != null) {
            activityToUpdate.setDueDate(activityDTO.getDueDate());
        }
        if (activityDTO.getDueTime() != null) {
            activityToUpdate.setDueTime(activityDTO.getDueTime());
        }
        if (activityDTO.getStatus() != null) {
            activityToUpdate.setStatus(activityDTO.getStatus());
        }
        if (activityDTO.getMeetingLink() != null) {
            activityToUpdate.setMeetingLink(activityDTO.getMeetingLink());
        }
        if (activityDTO.getAttendees() != null) {
            activityToUpdate.setAttendees(activityDTO.getAttendees());
        }
        if (activityDTO.getCallPurpose() != null) {
            activityToUpdate.setCallPurpose(activityDTO.getCallPurpose());
        }
        if (activityDTO.getCallOutcome() != null) {
            activityToUpdate.setCallOutcome(activityDTO.getCallOutcome());
        }
        if (activityDTO.getNextFollowUpDate() != null) {
            activityToUpdate.setNextFollowUpDate(activityDTO.getNextFollowUpDate());
        }
        if (activityDTO.getNextFollowUpTime() != null) {
            activityToUpdate.setNextFollowUpTime(activityDTO.getNextFollowUpTime());
        }
        if (activityDTO.getMeetingVenue() != null) {
            activityToUpdate.setMeetingVenue(activityDTO.getMeetingVenue());
        }
        if (activityDTO.getNote() != null) {
            activityToUpdate.setNote(activityDTO.getNote());
        }
        if (activityDTO.getAttachment() != null) {
            activityToUpdate.setAttachment(activityDTO.getAttachment());
        }
        
        logActivityCompletion(lead, activityToUpdate, user);
        
        return leadRepository.save(lead);
    }

    public LeadModel deleteActivity(String leadId, String activityId) {
        LeadModel lead = getLeadByLeadId(leadId);
        if (lead.getActivities() == null) {
            throw new RuntimeException(" No activities found for this lead");
        }
        Activity deletedActivity = lead.getActivities().stream()
            .filter(activity -> activity.getId().equals(activityId))
            .findFirst()
            .orElse(null);
        boolean removed = lead.getActivities().removeIf(activity -> activity.getId().equals(activityId));
        if (!removed) {
            throw new RuntimeException(" Activity not found with id: " + activityId);
        }
        // Add ActivityLog for deletion
        if (deletedActivity != null) {
            if (lead.getActivityLogs() == null) {
                lead.setActivityLogs(new java.util.ArrayList<>());
            }
            ActivityLog log = new ActivityLog();
            log.setId("LOG-" + snowflakeIdGenerator.nextId());
            log.setAction("Activity Deleted");
            log.setDetails(deletedActivity.getType() + ": " + (deletedActivity.getNotes() != null ? deletedActivity.getNotes() : ""));
            log.setUser(lead.getSubmittedBy() != null ? lead.getSubmittedBy() : "System");
            log.setTimestamp(java.time.LocalDateTime.now().toString());
            log.setActivityType(deletedActivity.getType());
            log.setMetadata(new java.util.HashMap<>());
            lead.getActivityLogs().add(log);
        }
        return leadRepository.save(lead);
    }

    public String getActivityStatus(String leadId, String activityId) {
        LeadModel lead = getLeadByLeadId(leadId);
        
        if (lead.getActivities() == null) {
            throw new RuntimeException(" No activities found for this lead");
        }
        
        Activity activity = lead.getActivities().stream()
                .filter(act -> act.getId().equals(activityId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(" Activity not found with id: " + activityId));
        
        return activity.getStatus();
    }

    public LeadModel updateActivityStatus(String leadId, String activityId, String status) {
        LeadModel lead = getLeadByLeadId(leadId);
        
        if (lead.getActivities() == null) {
            throw new RuntimeException(" No activities found for this lead");
        }
        
        Activity activity = lead.getActivities().stream()
                .filter(act -> act.getId().equals(activityId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(" Activity not found with id: " + activityId));
        
        String oldStatus = activity.getStatus();
        activity.setStatus(status);
        // Log when activity is completed or done
        if (("completed".equalsIgnoreCase(status) || "done".equalsIgnoreCase(status)) &&
            !("completed".equalsIgnoreCase(oldStatus) || "done".equalsIgnoreCase(oldStatus))) {
            logActivityCompletion(lead, activity, lead.getSubmittedBy() != null ? lead.getSubmittedBy() : "System");
        }
        
        return leadRepository.save(lead);
    }

    public LeadModel addNote(String leadId, NoteDTO noteDTO, String user) {
        LeadModel lead = getLeadByLeadId(leadId);
        
        if (lead.getNotesList() == null) {
            lead.setNotesList(new ArrayList<>());
        }
        
        Note note = new Note();
        note.setId("NOTE-" + snowflakeIdGenerator.nextId());
        note.setContent(noteDTO.getContent());
        note.setUser(user);
        note.setTimestamp(LocalDateTime.now().toString());
        
        lead.getNotesList().add(note);
        
        return leadRepository.save(lead);
    }

    public LeadModel updateNote(String leadId, String noteId, NoteDTO noteDTO, String user) {
        LeadModel lead = getLeadByLeadId(leadId);
        
        if (lead.getNotesList() == null) {
            throw new RuntimeException(" No notes found for this lead");
        }
        
        Note noteToUpdate = lead.getNotesList().stream()
                .filter(note -> note.getId().equals(noteId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(" Note not found with id: " + noteId));
        
        if (noteDTO.getContent() != null) {
            noteToUpdate.setContent(noteDTO.getContent());
        }
        
        return leadRepository.save(lead);
    }

    public LeadModel deleteNote(String leadId, String noteId) {
        LeadModel lead = getLeadByLeadId(leadId);
        
        if (lead.getNotesList() == null) {
            throw new RuntimeException(" No notes found for this lead");
        }
        
        boolean removed = lead.getNotesList().removeIf(note -> note.getId().equals(noteId));
        
        if (!removed) {
            throw new RuntimeException(" Note not found with id: " + noteId);
        }
        
        return leadRepository.save(lead);
    }

    public List<ActivityLog> getActivityLogs(String leadId) {
        LeadModel lead = getLeadByLeadId(leadId);
        return lead.getActivityLogs() != null ? lead.getActivityLogs() : new ArrayList<>();
    }

    public LeadModel addActivityLog(String leadId, ActivityLogRequestDTO activityLogDTO, String user) {
        LeadModel lead = getLeadByLeadId(leadId);
        
        if (lead.getActivityLogs() == null) {
            lead.setActivityLogs(new ArrayList<>());
        }
        
        ActivityLog log = new ActivityLog();
        log.setId(activityLogDTO.getId() != null ? activityLogDTO.getId() : "LOG-" + snowflakeIdGenerator.nextId());
        log.setAction(activityLogDTO.getAction());
        log.setDetails(activityLogDTO.getDetails());
        log.setUser(activityLogDTO.getUser() != null ? activityLogDTO.getUser() : user);
        log.setTimestamp(activityLogDTO.getTimestamp() != null ? activityLogDTO.getTimestamp() : LocalDateTime.now().toString());
        log.setActivityType(activityLogDTO.getActivityType() != null ? activityLogDTO.getActivityType() : "USER_ACTION");
        if (activityLogDTO.getMetadata() != null) {
            log.setMetadata(activityLogDTO.getMetadata());
        }
        
        lead.getActivityLogs().add(log);
        
        return leadRepository.save(lead);
    }

    public LeadModel uploadPaymentDetails(String leadId, MultipartFile file, String user) {
        LeadModel lead = getLeadByLeadId(leadId);
        
        String fileName = file.getOriginalFilename();
        minioService.uploadFile("documents", file, leadId);
        
        lead.setPaymentDetailsFileName(fileName);
        
        return leadRepository.save(lead);
    }

    public LeadModel uploadBookingForm(String leadId, MultipartFile file, String user) {
        LeadModel lead = getLeadByLeadId(leadId);
        
        String fileName = file.getOriginalFilename();
        minioService.uploadFile("documents", file, leadId);
        
        lead.setBookingFormFileName(fileName);
        
        return leadRepository.save(lead);
    }

    public ConversionStatsResponse getConversionStats() {
        return getConversionStats(null);
    }

    public ConversionStatsResponse getConversionStatsForSales(String employeeId) {
        return getConversionStats(employeeId);
    }

    // Consolidated statistics method
    public ConversionStatsResponse getConversionStats(String employeeId) {
        List<LeadModel> leads = employeeId != null ? 
            getLeadsForSales(employeeId) : getAllLeads();
        
        ConversionStatsResponse stats = new ConversionStatsResponse();
        stats.setTotalLeads(leads.size());
        
        // Get stage names for counting
        var newStage = pipelineStageService.getStageByName("New");
        var contactedStage = pipelineStageService.getStageByName("Contacted");
        var qualifiedStage = pipelineStageService.getStageByName("Qualified");
        var quotedStage = pipelineStageService.getStageByName("Quoted");
        var convertedStage = pipelineStageService.getStageByName("Converted");
        var lostStage = pipelineStageService.getStageByName("Lost");
        
        for (LeadModel lead : leads) {
            if (newStage.isPresent() && newStage.get().getStageId().equals(lead.getStageId())) {
                stats.setNewLeads(stats.getNewLeads() + 1);
            } else if (contactedStage.isPresent() && contactedStage.get().getStageId().equals(lead.getStageId())) {
                stats.setContactedLeads(stats.getContactedLeads() + 1);
            } else if (qualifiedStage.isPresent() && qualifiedStage.get().getStageId().equals(lead.getStageId())) {
                stats.setQualifiedLeads(stats.getQualifiedLeads() + 1);
            } else if (quotedStage.isPresent() && quotedStage.get().getStageId().equals(lead.getStageId())) {
                stats.setQuotedLeads(stats.getQuotedLeads() + 1);
            } else if (convertedStage.isPresent() && convertedStage.get().getStageId().equals(lead.getStageId())) {
                stats.setConvertedLeads(stats.getConvertedLeads() + 1);
            } else if (lostStage.isPresent() && lostStage.get().getStageId().equals(lead.getStageId())) {
                stats.setLostLeads(stats.getLostLeads() + 1);
            }
        }
        
        // Calculate conversion rate
        if (stats.getTotalLeads() > 0) {
            stats.setConversionRate((double) stats.getConvertedLeads() / stats.getTotalLeads() * 100);
        }
        
        return stats;
    }

    public List<CompletedActivityResponse> getAllCompletedActivities() {
        return getCompletedActivitiesForSales(null);
    }

    public List<CompletedActivityResponse> getCompletedActivitiesForSales(String employeeId) {
        List<LeadModel> leads = employeeId != null ? 
            getLeadsForSales(employeeId) : getAllLeads();
        List<CompletedActivityResponse> completedActivities = new ArrayList<>();
        
        for (LeadModel lead : leads) {
            if (lead.getActivities() != null) {
                for (Activity activity : lead.getActivities()) {
                    if ("completed".equalsIgnoreCase(activity.getStatus())) {
                        CompletedActivityResponse response = new CompletedActivityResponse();
                        response.setLeadId(lead.getLeadId());
                        response.setLeadName(lead.getName());
                        response.setActivity(activity);
                        completedActivities.add(response);
                    }
                }
            }
        }
        
        return completedActivities;
    }

    public static class CompletedActivityResponse {
        private String leadId;
        private String leadName;
        private Activity activity;

        // Getters and setters
        public String getLeadId() { return leadId; }
        public void setLeadId(String leadId) { this.leadId = leadId; }
        public String getLeadName() { return leadName; }
        public void setLeadName(String leadName) { this.leadName = leadName; }
        public Activity getActivity() { return activity; }
        public void setActivity(Activity activity) { this.activity = activity; }
    }

    public static class ConversionStatsResponse {
        private int totalLeads;
        private int convertedLeads;
        private int quotedLeads;
        private int qualifiedLeads;
        private int contactedLeads;
        private int newLeads;
        private int lostLeads;
        private double conversionRate;

        // Getters and setters
        public int getTotalLeads() { return totalLeads; }
        public void setTotalLeads(int totalLeads) { this.totalLeads = totalLeads; }
        public int getConvertedLeads() { return convertedLeads; }
        public void setConvertedLeads(int convertedLeads) { this.convertedLeads = convertedLeads; }
        public int getQuotedLeads() { return quotedLeads; }
        public void setQuotedLeads(int quotedLeads) { this.quotedLeads = quotedLeads; }
        public int getQualifiedLeads() { return qualifiedLeads; }
        public void setQualifiedLeads(int qualifiedLeads) { this.qualifiedLeads = qualifiedLeads; }
        public int getContactedLeads() { return contactedLeads; }
        public void setContactedLeads(int contactedLeads) { this.contactedLeads = contactedLeads; }
        public int getNewLeads() { return newLeads; }
        public void setNewLeads(int newLeads) { this.newLeads = newLeads; }
        public int getLostLeads() { return lostLeads; }
        public void setLostLeads(int lostLeads) { this.lostLeads = lostLeads; }
        public double getConversionRate() { return conversionRate; }
        public void setConversionRate(double conversionRate) { this.conversionRate = conversionRate; }
    }

    public List<LeadModel> getLeadsWithFilters(String stageId, String employeeId) {
        if (stageId != null && employeeId != null) {
            return getLeadsForSalesByStageId(employeeId, stageId);
        } else if (stageId != null) {
            return getLeadsByStageId(stageId);
        } else if (employeeId != null) {
            return getLeadsForSales(employeeId);
        } else {
            return getAllLeads();
        }
    }

    public LeadConversionResponseDTO convertLeadWithResponse(String leadId, ConvertLeadRequestDTO conversionData, String user) {
        LeadModel convertedLead = convertLead(leadId, conversionData, user);
        
        LeadConversionResponseDTO response = new LeadConversionResponseDTO();
        response.setLeadId(convertedLead.getLeadId());
        response.setLeadName(convertedLead.getName());
        response.setFinalQuotation(convertedLead.getFinalQuotation());
        response.setSignupAmount(convertedLead.getSignupAmount());
        response.setPaymentDate(convertedLead.getPaymentDate());
        response.setPaymentMode(convertedLead.getPaymentMode());
        response.setPanNumber(convertedLead.getPanNumber());
        response.setDiscount(convertedLead.getDiscount());
        response.setInitialQuote(convertedLead.getInitialQuote());
        response.setProjectTimeline(convertedLead.getProjectTimeline());
        response.setConversionNoteId(findConversionNoteId(convertedLead));
        response.setConversionTimestamp(LocalDateTime.now().toString());
        response.setConvertedBy(user);
        
        return response;
    }

    private String findConversionNoteId(LeadModel convertedLead) {
        if (convertedLead.getNotesList() != null) {
            for (Note note : convertedLead.getNotesList()) {
                if (note.getContent().startsWith(" LEAD CONVERTED:")) {
                    return note.getId();
                }
            }
        }
        return null;
    }

    public LeadModel addActivitiesBulk(String leadId, List<ActivityDTO> activities, String user) {
        LeadModel lead = getLeadByLeadId(leadId);
        if (lead.getActivities() == null) {
            lead.setActivities(new ArrayList<>());
        }
        for (ActivityDTO activityDTO : activities) {
            Activity activity = new Activity();
            activity.setId("ACT-" + snowflakeIdGenerator.nextId());
            activity.setType(activityDTO.getType());
            activity.setTitle(activityDTO.getTitle());
            activity.setNotes(activityDTO.getNotes());
            activity.setDueDate(activityDTO.getDueDate());
            activity.setDueTime(activityDTO.getDueTime());
            activity.setUser(user);
            activity.setStatus(activityDTO.getStatus());
            activity.setMeetingLink(activityDTO.getMeetingLink());
            activity.setAttendees(activityDTO.getAttendees());
            activity.setCallPurpose(activityDTO.getCallPurpose());
            activity.setCallOutcome(activityDTO.getCallOutcome());
            activity.setNextFollowUpDate(activityDTO.getNextFollowUpDate());
            activity.setNextFollowUpTime(activityDTO.getNextFollowUpTime());
            activity.setMeetingVenue(activityDTO.getMeetingVenue());
            activity.setNote(activityDTO.getNote());
            activity.setAttachment(activityDTO.getAttachment());
            lead.getActivities().add(activity);
            // Log activity creation
            if (lead.getActivityLogs() == null) {
                lead.setActivityLogs(new ArrayList<>());
            }
            ActivityLog log = new ActivityLog();
            log.setId("LOG-" + snowflakeIdGenerator.nextId());
            log.setAction("Activity created");
            log.setDetails(activity.getTitle());
            log.setUser(user);
            log.setTimestamp(LocalDateTime.now().toString());
            log.setActivityType(activity.getType());
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("activityId", activity.getId());
            metadata.put("activityTitle", activity.getTitle());
            metadata.put("activityNotes", activity.getNotes());
            metadata.put("activityType", activity.getType());
            log.setMetadata(metadata);
            lead.getActivityLogs().add(log);
        }
        createLead(lead); // Save updated lead
        return lead;
    }

    public LeadModel addActivitiesBulkWithFiles(String leadId, List<ActivityDTO> activities, List<MultipartFile> files, String user) {
        LeadModel lead = getLeadByLeadId(leadId);
        if (lead.getActivities() == null) {
            lead.setActivities(new ArrayList<>());
        }
        for (int i = 0; i < activities.size(); i++) {
            ActivityDTO activityDTO = activities.get(i);
            Activity activity = new Activity();
            activity.setId("ACT-" + snowflakeIdGenerator.nextId());
            activity.setType(activityDTO.getType());
            activity.setTitle(activityDTO.getTitle());
            activity.setNotes(activityDTO.getNotes());
            activity.setDueDate(activityDTO.getDueDate());
            activity.setDueTime(activityDTO.getDueTime());
            activity.setUser(user);
            activity.setStatus(activityDTO.getStatus());
            activity.setMeetingLink(activityDTO.getMeetingLink());
            activity.setAttendees(activityDTO.getAttendees());
            activity.setCallPurpose(activityDTO.getCallPurpose());
            activity.setCallOutcome(activityDTO.getCallOutcome());
            activity.setNextFollowUpDate(activityDTO.getNextFollowUpDate());
            activity.setNextFollowUpTime(activityDTO.getNextFollowUpTime());
            activity.setMeetingVenue(activityDTO.getMeetingVenue());
            activity.setNote(activityDTO.getNote());
            // Handle file upload if file is provided for this activity
            if (files != null && files.size() > i && files.get(i) != null && !files.get(i).isEmpty()) {
                String fileUrl = minioService.uploadDocumentsImg(files.get(i), leadId);
                activity.setAttachment(fileUrl);
            } else {
                activity.setAttachment(activityDTO.getAttachment());
            }
            lead.getActivities().add(activity);
            // Log activity creation
            if (lead.getActivityLogs() == null) {
                lead.setActivityLogs(new ArrayList<>());
            }
            ActivityLog log = new ActivityLog();
            log.setId("LOG-" + snowflakeIdGenerator.nextId());
            log.setAction("Activity created");
            log.setDetails(activity.getTitle());
            log.setUser(user);
            log.setTimestamp(LocalDateTime.now().toString());
            log.setActivityType(activity.getType());
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("activityId", activity.getId());
            metadata.put("activityTitle", activity.getTitle());
            metadata.put("activityNotes", activity.getNotes());
            metadata.put("activityType", activity.getType());
            log.setMetadata(metadata);
            lead.getActivityLogs().add(log);
        }
        createLead(lead); // Save updated lead
        return lead;
    }

    public LeadConversionResponseDTO convertLeadWithDocs(
        String leadId,
        ConvertLeadRequestDTO conversionData,
        MultipartFile paymentDetailsFile,
        MultipartFile bookingFormFile,
        String user
    ) {
        LeadModel lead = getLeadByLeadId(leadId);

        // Upload and set payment details file
        if (paymentDetailsFile != null && !paymentDetailsFile.isEmpty()) {
            String paymentFileUrl = minioService.uploadDocumentsImg(paymentDetailsFile, leadId);
            lead.setPaymentDetailsFileName(paymentFileUrl);
        }

        // Upload and set booking form file
        if (bookingFormFile != null && !bookingFormFile.isEmpty()) {
            String bookingFileUrl = minioService.uploadDocumentsImg(bookingFormFile, leadId);
            lead.setBookingFormFileName(bookingFileUrl);
        }

        // Save the lead after setting file fields
        leadRepository.save(lead);

        // Existing conversion logic
        LeadModel convertedLead = convertLead(leadId, conversionData, user);

        // Build and return response DTO as before
        LeadConversionResponseDTO response = new LeadConversionResponseDTO();
        response.setLeadId(convertedLead.getLeadId());
        response.setLeadName(convertedLead.getName());
        response.setFinalQuotation(convertedLead.getFinalQuotation());
        response.setSignupAmount(convertedLead.getSignupAmount());
        response.setPaymentDate(convertedLead.getPaymentDate());
        response.setPaymentMode(convertedLead.getPaymentMode());
        response.setPanNumber(convertedLead.getPanNumber());
        response.setDiscount(convertedLead.getDiscount());
        response.setInitialQuote(convertedLead.getInitialQuote());
        response.setProjectTimeline(convertedLead.getProjectTimeline());
        response.setConversionNoteId(findConversionNoteId(convertedLead));
        response.setConversionTimestamp(java.time.LocalDateTime.now().toString());
        response.setConvertedBy(user);
        return response;
    }

    public static class KanbanLeadDTO {
        private String leadId;
        private String name;
        private String salesRep;
        private String designer;
        private String priority;
        private String dateOfCreation;
        private String budget;
        // getters and setters
        public String getLeadId() { return leadId; }
        public void setLeadId(String leadId) { this.leadId = leadId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSalesRep() { return salesRep; }
        public void setSalesRep(String salesRep) { this.salesRep = salesRep; }
        public String getDesigner() { return designer; }
        public void setDesigner(String designer) { this.designer = designer; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public String getDateOfCreation() { return dateOfCreation; }
        public void setDateOfCreation(String dateOfCreation) { this.dateOfCreation = dateOfCreation; }
        public String getBudget() { return budget; }
        public void setBudget(String budget) { this.budget = budget; }
    }

    public static class KanbanStageGroupDTO {
        private String stageId;
        private List<KanbanLeadDTO> leads;
        public KanbanStageGroupDTO(String stageId, List<KanbanLeadDTO> leads) {
            this.stageId = stageId;
            this.leads = leads;
        }
        public String getStageId() { return stageId; }
        public void setStageId(String stageId) { this.stageId = stageId; }
        public List<KanbanLeadDTO> getLeads() { return leads; }
        public void setLeads(List<KanbanLeadDTO> leads) { this.leads = leads; }
    }

    public List<KanbanStageGroupDTO> getKanbanLeadsForBoard() {
        List<KanbanLeadProjection> projections = leadRepository.findAllBy(KanbanLeadProjection.class);
        // Map to DTOs, but do not include stageId in the lead object
        List<KanbanLeadDTO> dtos = projections.stream().map(p -> {
            KanbanLeadDTO dto = new KanbanLeadDTO();
            dto.setLeadId(p.getLeadId());
            dto.setName(p.getName());
            dto.setSalesRep(p.getSalesRep());
            dto.setDesigner(p.getDesigner());
            dto.setPriority(p.getPriority());
            dto.setDateOfCreation(p.getDateOfCreation());
            dto.setBudget(p.getBudget());
            return dto;
        }).collect(Collectors.toList());
        // Group by stageId
        return projections.stream()
            .map(KanbanLeadProjection::getStageId)
            .distinct()
            .map(stageId -> new KanbanStageGroupDTO(stageId,
                dtos.stream()
                    .filter(dto -> projections.stream()
                        .filter(p -> p.getLeadId().equals(dto.getLeadId()))
                        .findFirst().map(KanbanLeadProjection::getStageId).orElse(null)
                        .equals(stageId))
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());
    }



    public List<LeadProjectCustomerResponseDTO> getAllProjectCustomerInfo() {
        List<LeadModel> leads = leadRepository.findAll();

        return leads.stream()
                .filter(lead -> lead.getCustomerId() != null) // skip bad data
                .map(lead -> new LeadProjectCustomerResponseDTO(
                        lead.getLeadId(),
                        lead.getProjectName(),
                        lead.getCustomerId(),
                        lead.getName()
                ))
                .collect(Collectors.toList());
    }


}
