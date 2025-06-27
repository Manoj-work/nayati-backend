package com.medhir.rest.sales;

import com.medhir.rest.sales.dto.ConvertedLeadRequest;
import com.medhir.rest.sales.dto.JunkLeadRequest;
import com.medhir.rest.sales.dto.LostLeadRequest;
import com.medhir.rest.utils.MinioService;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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

    // -------------------- Lead Creation --------------------
    public ModelLead createLead(ModelLead lead) {
        if (leadRepository.findByContactNumber(lead.getContactNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A lead with phone number '" + lead.getContactNumber() + "' already exists");
        }
        if (leadRepository.findByEmail(lead.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A lead with email '" + lead.getEmail() + "' already exists");
        }
        validateManagerFields(lead);

        try {
            lead.setLeadId("LID" + snowflakeIdGenerator.nextId());

            // Handle notes: assign noteId, user (from role), timestamp for each note
            if (lead.getNotes() != null) {
                for (ModelLead.Note note : lead.getNotes()) {
                    note.setNoteId("NID" + snowflakeIdGenerator.nextId());
                    note.setUser(lead.getRole() != null ? lead.getRole().name() : "UNKNOWN");
                    note.setTimestamp(LocalDateTime.now().toString());
                }
            }

            // Log creation in activity log
            addActivityLogEntry(lead, "lead", null, "New", "Lead created", lead.getRole() != null ? lead.getRole().name() : "SYSTEM");
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

    public List<ModelLead> getLeadsByStatus(String status) {
        return leadRepository.findByStatusIgnoreCase(status);
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
        existing.setStatus(updatedLead.getStatus());
        existing.setDesignStyle(updatedLead.getDesignStyle());
        existing.setRole(updatedLead.getRole());
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
        existing.setReasonForLoss(updatedLead.getReasonForLoss());
        existing.setReasonForMarkingAsJunk(updatedLead.getReasonForMarkingAsJunk());

        // DO NOT overwrite activities, notes, or activityLog!
        validateManagerFields(existing);
        return leadRepository.save(existing);
    }

    // -------------------- Lead Status Change & Logging --------------------
    public ModelLead updateLeadStatus(String leadId, String newStatus) {
        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));

        List<String> allowedStatuses = List.of("new", "connected", "qualified", "quoted", "converted", "Lost", "Junk");
        if (!allowedStatuses.contains(newStatus.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + newStatus);
        }

        String oldStatus = lead.getStatus();
        lead.setStatus(newStatus);

        // Log the status change in the activity log as type "lead"
        addActivityLogEntry(lead, "lead", oldStatus, newStatus,
                "Lead status changed from " + oldStatus + " to " + newStatus,
                lead.getRole() != null ? lead.getRole().name() : "SYSTEM");

        return leadRepository.save(lead);
    }

    // -------------------- Notes Management --------------------
    public ModelLead.Note addNoteToLead(String leadId, String content) {
        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));

        ModelLead.Note note = new ModelLead.Note();
        note.setNoteId("NID" + snowflakeIdGenerator.nextId());
        note.setContent(content);
        note.setUser(lead.getRole() != null ? lead.getRole().name() : "UNKNOWN");
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
    public ModelLead.ActivityDetails addOrUpdateActivity(String leadId, ModelLead.ActivityDetails activity, String performedBy) {
        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));

        boolean isNew = (activity.getActivityId() == null);
        if (isNew) {
            activity.setActivityId("AID" + snowflakeIdGenerator.nextId());
            if (lead.getActivities() == null) lead.setActivities(new ArrayList<>());
            lead.getActivities().add(activity);
        } else {
            // Update existing activity logic
            List<ModelLead.ActivityDetails> activities = lead.getActivities();
            for (int i = 0; i < activities.size(); i++) {
                if (activities.get(i).getActivityId().equals(activity.getActivityId())) {
                    activities.set(i, activity);
                    break;
                }
            }
        }

        // Log activity status changes only for "pending" or "done"
        String status = activity.getStatus();
        if ("pending".equalsIgnoreCase(status) || "done".equalsIgnoreCase(status)) {
            addActivityLogEntry(lead, "activity", null, status,
                    "Activity '" + activity.getSummary() + "' marked as " + status,
                    performedBy != null ? performedBy : (lead.getRole() != null ? lead.getRole().name() : "SYSTEM"));
        }

        leadRepository.save(lead);
        return activity;
    }

    public List<ModelLead.ActivityDetails> getAllActivities(String leadId) {
        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
        return lead.getActivities() != null ? lead.getActivities() : new ArrayList<>();
    }

    public Optional<ModelLead.ActivityDetails> getActivityById(String leadId, String activityId) {
        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
        if (lead.getActivities() == null) return Optional.empty();
        return lead.getActivities().stream()
                .filter(a -> activityId.equals(a.getActivityId()))
                .findFirst();
    }

    public ModelLead.ActivityDetails updateActivity(String leadId, String activityId, ModelLead.ActivityDetails activity) {
        activity.setActivityId(activityId);
        return addOrUpdateActivity(leadId, activity, null);
    }

    public void deleteActivity(String leadId, String activityId) {
        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
        if (lead.getActivities() != null) {
            lead.getActivities().removeIf(a -> activityId.equals(a.getActivityId()));
            leadRepository.save(lead);
        }
    }

    // -------------------- Activity Log Helper --------------------
    private void addActivityLogEntry(ModelLead lead, String type, String previousStatus, String status,
                                     String summary, String performedBy) {
        // 1. Get existing logs or initialize if null
        List<ModelLead.ActivityLogEntry> logs = lead.getActivityLog();
        if (logs == null) {
            logs = new ArrayList<>();
            lead.setActivityLog(logs); // Attach to lead if new
        }

        // 2. Create new log entry
        ModelLead.ActivityLogEntry logEntry = new ModelLead.ActivityLogEntry();
        logEntry.setId("LOG-" + snowflakeIdGenerator.nextId());
        logEntry.setType(type);
        logEntry.setPreviousStatus(previousStatus);
        logEntry.setStatus(status);
        logEntry.setSummary(summary);
        logEntry.setPerformedBy(performedBy);
        logEntry.setTimestamp(LocalDateTime.now().toString());

        // 3. Append to existing logs (not replace)
        logs.add(logEntry);
    }


    // -------------------- Validation --------------------
    private void validateManagerFields(ModelLead lead) {
        if (lead.getRole() != null && lead.getRole().name().equalsIgnoreCase("MANAGER")) {
            if (isEmpty(lead.getAssignedSalesPerson()) || isEmpty(lead.getAssignedDesigner())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Manager must have both assignedSalesPerson and assignedDesigner.");
            }
        }
    }

    private boolean isEmpty(String val) {
        return val == null || val.trim().isEmpty();
    }

    // ===================== Kanban Status-Specific Methods =====================

    /**
     * Convert a lead with detailed fields and file uploads.
     */
    public void convertLead(String leadId, ConvertedLeadRequest request) {
        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));

        lead.setInitialQuotedAmount(request.getInitialQuotedAmount());
        lead.setFinalQuotation(request.getFinalQuotation());
        lead.setSignUpAmount(request.getSignUpAmount());
        lead.setPaymentDate(request.getPaymentDate());
        lead.setPaymentMode(request.getPaymentMode());
        lead.setPanNumber(request.getPanNumber());
        lead.setProjectTimeline(request.getProjectTimeline());
        lead.setDiscount(request.getDiscount());

        // Handle file uploads to MinIO
        // Use the document bucket for Kanban-related uploads
        String bucketName = minioService.getDocumentBucketName();
        MultipartFile paymentDetailsFile = request.getPaymentDetailsFile();
        MultipartFile bookingFormFile = request.getBookingFormFile();

        if (paymentDetailsFile != null && !paymentDetailsFile.isEmpty()) {
            String paymentDetailsUrl = minioService.uploadFile(bucketName, paymentDetailsFile, leadId);
            lead.setPaymentDetailsFile(paymentDetailsUrl);
        }
        if (bookingFormFile != null && !bookingFormFile.isEmpty()) {
            String bookingFormUrl = minioService.uploadFile(bucketName, bookingFormFile, leadId);
            lead.setBookingFormFile(bookingFormUrl);
        }

        lead.setStatus("converted");
        leadRepository.save(lead);
    }

    public void markLeadAsLost(String leadId, LostLeadRequest request) {
        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));

        String oldStatus = lead.getStatus();
        lead.setStatus("Lost");
        lead.setReasonForLoss(request.getReasonForLoss());

        // Add activity log entry
        addActivityLogEntry(
                lead,
                "lead",
                oldStatus,
                "Lost",
                "Lead marked as Lost with reason: " + request.getReasonForLoss(),
                lead.getRole() != null ? lead.getRole().name() : "SYSTEM"
        );

        leadRepository.save(lead);
    }


    public void markLeadAsJunk(String leadId, JunkLeadRequest request) {
        ModelLead lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));

        String oldStatus = lead.getStatus();
        lead.setStatus("Junk");
        lead.setReasonForMarkingAsJunk(request.getReasonForMarkingAsJunk());

        // Add activity log entry
        addActivityLogEntry(
                lead,
                "lead",
                oldStatus,
                "Junk",
                "Lead marked as Junk with reason: " + request.getReasonForMarkingAsJunk(),
                lead.getRole() != null ? lead.getRole().name() : "SYSTEM"
        );

        leadRepository.save(lead);
    }

}
