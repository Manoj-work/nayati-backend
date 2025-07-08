package com.medhir.rest.sales.controller;
import com.medhir.rest.sales.dto.lead.LeadRequestDTO;
import com.medhir.rest.sales.dto.activity.ActivityLogRequestDTO;
import com.medhir.rest.sales.dto.lead.ConvertLeadRequestDTO;
import com.medhir.rest.sales.dto.lead.LeadConversionResponseDTO;
import com.medhir.rest.sales.dto.lead.LeadAssignmentRequestDTO;

import com.medhir.rest.sales.service.LeadService;
import com.medhir.rest.sales.dto.activity.ActivityDTO;
import com.medhir.rest.sales.dto.activity.NoteDTO;
import com.medhir.rest.sales.dto.activity.ActivityLogDTO;
import org.springframework.web.multipart.MultipartFile;
import com.medhir.rest.sales.dto.lead.LeadResponseDTO;
import com.medhir.rest.sales.mapper.LeadMapper;
import com.medhir.rest.sales.service.PipelineStageService;
import com.medhir.rest.service.EmployeeService;
import com.medhir.rest.utils.MinioService;
import com.medhir.rest.sales.model.LeadModel;
import com.medhir.rest.sales.model.Activity;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestPart;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/leads")
@CrossOrigin
public class LeadController {

    @Autowired
    private LeadService leadService;

//    just testing
    @Autowired
    private PipelineStageService pipelineStageService;
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private MinioService minioService;

    // 🔍 Employee-Specific Lead Operations
    @GetMapping("/my")
    public List<LeadResponseDTO> getMyLeads(@RequestParam String employeeId) {
        return leadService.getLeadsForSales(employeeId).stream()
            .map(lead -> LeadMapper.mapToResponseDTO(lead, pipelineStageService, employeeService))
            .toList();
    }

    @GetMapping("/my/stage/{stageId}")
    public List<LeadResponseDTO> getMyLeadsByStageIdPath(@RequestParam String employeeId, @PathVariable String stageId) {
        return leadService.getLeadsForSalesByStageId(employeeId, stageId).stream()
            .map(lead -> LeadMapper.mapToResponseDTO(lead, pipelineStageService, employeeService))
            .toList();
    }

    // 🔍 Manager/Admin Lead Operations (sees all leads)
    @GetMapping
    public List<LeadResponseDTO> getAllLeadsForManager(@RequestParam(value = "stageId", required = false) String stageId,
                                                 @RequestParam(value = "employeeId", required = false) String employeeId) {
        return leadService.getLeadsWithFilters(stageId, employeeId).stream()
            .map(lead -> LeadMapper.mapToResponseDTO(lead, pipelineStageService, employeeService))
            .toList();
    }

    @PostMapping
    public LeadResponseDTO createLead(@Valid @RequestBody LeadRequestDTO dto) {
        return LeadMapper.mapToResponseDTO(leadService.createLead(dto), pipelineStageService, employeeService);
    }

    @PutMapping("/{leadId}")
    public LeadResponseDTO updateLead(@PathVariable String leadId, @Valid @RequestBody LeadRequestDTO dto) {
        return LeadMapper.mapToResponseDTO(leadService.updateLead(leadId, dto), pipelineStageService, employeeService);
    }

    @GetMapping("/{leadId}")
    public LeadResponseDTO getLeadById(@PathVariable String leadId) {
        return LeadMapper.mapToResponseDTO(leadService.getLeadByLeadId(leadId), pipelineStageService, employeeService);
    }

    @DeleteMapping("/{leadId}")
    public void deleteLead(@PathVariable String leadId) {
        leadService.deleteLead(leadId);
    }

    // 🎯 Activity Management
    @PostMapping("/{leadId}/activities")
    public LeadResponseDTO addActivity(@PathVariable String leadId, @RequestBody ActivityDTO activityDTO) {
        return LeadMapper.mapToResponseDTO(leadService.addActivity(leadId, activityDTO, "Public User"), pipelineStageService, employeeService);
    }

    @PutMapping("/{leadId}/activities/{activityId}")
    public LeadResponseDTO updateActivity(@PathVariable String leadId, @PathVariable String activityId, 
                                   @RequestBody ActivityDTO activityDTO) {
        return LeadMapper.mapToResponseDTO(leadService.updateActivity(leadId, activityId, activityDTO, "Public User"), pipelineStageService, employeeService);
    }

    @DeleteMapping("/{leadId}/activities/{activityId}")
    public LeadResponseDTO deleteActivity(@PathVariable String leadId, @PathVariable String activityId) {
        return LeadMapper.mapToResponseDTO(leadService.deleteActivity(leadId, activityId), pipelineStageService, employeeService);
    }

    @GetMapping("/{leadId}/activities/{activityId}/status")
    public String getActivityStatus(@PathVariable String leadId, @PathVariable String activityId) {
        return leadService.getActivityStatus(leadId, activityId);
    }

    @PatchMapping("/{leadId}/activities/{activityId}/status")
    public LeadResponseDTO updateActivityStatus(@PathVariable String leadId, @PathVariable String activityId, 
                                        @RequestBody String status) {
        // Remove extra quotes if present
        if (status != null && status.startsWith("\"") && status.endsWith("\"")) {
            status = status.substring(1, status.length() - 1);
        }
        return LeadMapper.mapToResponseDTO(leadService.updateActivityStatus(leadId, activityId, status), pipelineStageService, employeeService);
    }

    // 🎯 Note Management
    @PostMapping("/{leadId}/notes")
    public LeadResponseDTO addNote(@PathVariable String leadId, @RequestBody NoteDTO noteDTO) {
        return LeadMapper.mapToResponseDTO(leadService.addNote(leadId, noteDTO, "Public User"), pipelineStageService, employeeService);
    }

    @PutMapping("/{leadId}/notes/{noteId}")
    public LeadResponseDTO updateNote(@PathVariable String leadId, @PathVariable String noteId, @RequestBody NoteDTO noteDTO) {
        return LeadMapper.mapToResponseDTO(leadService.updateNote(leadId, noteId, noteDTO, "Public User"), pipelineStageService, employeeService);
    }

    @DeleteMapping("/{leadId}/notes/{noteId}")
    public LeadResponseDTO deleteNote(@PathVariable String leadId, @PathVariable String noteId) {
        return LeadMapper.mapToResponseDTO(leadService.deleteNote(leadId, noteId), pipelineStageService, employeeService);
    }

    // 🎯 Stage Management (changed from status to stageId)
    @PatchMapping("/{leadId}/stage/{stageId}")
    public LeadResponseDTO updateLeadStageId(@PathVariable String leadId, @PathVariable String stageId) {
        if (stageId == null || stageId.trim().isEmpty()) {
            throw new RuntimeException("Stage ID is required");
        }
        return LeadMapper.mapToResponseDTO(leadService.updateLeadStageId(leadId, stageId.trim()), pipelineStageService, employeeService);
    }

    // 🎯 Lead Conversion
    @PostMapping("/{leadId}/convert")
    public LeadConversionResponseDTO convertLead(@PathVariable String leadId, @Valid @RequestBody ConvertLeadRequestDTO conversionData) {
        return leadService.convertLeadWithResponse(leadId, conversionData, "Public User");
    }

    @PostMapping(path = "/{leadId}/convert-with-docs", consumes = {"multipart/form-data"})
    public LeadConversionResponseDTO convertLeadWithDocs(
        @PathVariable String leadId,
        @RequestParam(value = "conversionData", required = false) String conversionDataJson,
        @RequestParam(value = "finalQuotation", required = false) String finalQuotation,
        @RequestParam(value = "signupAmount", required = false) String signupAmount,
        @RequestParam(value = "paymentDate", required = false) String paymentDate,
        @RequestParam(value = "paymentMode", required = false) String paymentMode,
        @RequestParam(value = "panNumber", required = false) String panNumber,
        @RequestParam(value = "discount", required = false) String discount,
        @RequestParam(value = "conversionNotes", required = false) String conversionNotes,
        @RequestParam(value = "initialQuote", required = false) String initialQuote,
        @RequestParam(value = "projectTimeline", required = false) String projectTimeline,
        @RequestParam(value = "paymentDetailsFile", required = false) MultipartFile paymentDetailsFile,
        @RequestParam(value = "bookingFormFile", required = false) MultipartFile bookingFormFile
    ) throws Exception {
        ConvertLeadRequestDTO conversionData;
        
        if (conversionDataJson != null && !conversionDataJson.trim().isEmpty()) {
            // Handle JSON string approach
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                conversionData = objectMapper.readValue(conversionDataJson, ConvertLeadRequestDTO.class);
            } catch (Exception e) {
                throw new RuntimeException("Invalid JSON format for conversionData: " + e.getMessage());
            }
        } else {
            // Handle individual form fields approach
            conversionData = new ConvertLeadRequestDTO();
            conversionData.setFinalQuotation(finalQuotation);
            conversionData.setSignupAmount(signupAmount);
            conversionData.setPaymentDate(paymentDate);
            conversionData.setPaymentMode(paymentMode);
            conversionData.setPanNumber(panNumber);
            conversionData.setDiscount(discount);
            conversionData.setConversionNotes(conversionNotes);
            conversionData.setInitialQuote(initialQuote);
            conversionData.setProjectTimeline(projectTimeline);
            
            // Validate required fields
            if (finalQuotation == null || finalQuotation.trim().isEmpty()) {
                throw new RuntimeException("finalQuotation is required");
            }
            if (signupAmount == null || signupAmount.trim().isEmpty()) {
                throw new RuntimeException("signupAmount is required");
            }
        }
        
        return leadService.convertLeadWithDocs(leadId, conversionData, paymentDetailsFile, bookingFormFile, "Public User");
    }

    // 📋 Activity Logs
    @GetMapping("/{leadId}/activity-logs")
    public List<ActivityLogDTO> getActivityLogs(@PathVariable String leadId) {
        return leadService.getActivityLogs(leadId).stream()
            .map(log -> new ActivityLogDTO(log.getId(), log.getAction(), log.getDetails(), log.getUser(), log.getTimestamp(), log.getActivityType(), log.getMetadata()))
            .toList();
    }

    @PostMapping("/{leadId}/activity-logs")
    public LeadResponseDTO addActivityLog(@PathVariable String leadId, @Valid @RequestBody ActivityLogRequestDTO activityLogDTO) {
        return LeadMapper.mapToResponseDTO(leadService.addActivityLog(leadId, activityLogDTO, "Public User"), pipelineStageService, employeeService);
    }

    // 📈 Statistics - Employee-specific
    @GetMapping("/my/activities/completed")
    public List<LeadService.CompletedActivityResponse> getMyCompletedActivities(@RequestParam String employeeId) {
        return leadService.getCompletedActivitiesForSales(employeeId);
    }

    // 📈 Statistics - Manager view (all activities)
    @GetMapping("/activities/completed")
    public List<LeadService.CompletedActivityResponse> getAllCompletedActivities() {
        return leadService.getAllCompletedActivities();
    }

    // 📁 File Uploads
    @PostMapping("/{leadId}/upload-payment-details")
    public LeadResponseDTO uploadPaymentDetails(@PathVariable String leadId, @RequestParam("file") MultipartFile file) {
        LeadModel lead = leadService.getLeadByLeadId(leadId);
        String fileUrl = minioService.uploadDocumentsImg(file, leadId);
        lead.setPaymentDetailsFileName(fileUrl);
        leadService.createLead(lead); // Save updated lead
        return LeadMapper.mapToResponseDTO(lead, pipelineStageService, employeeService);
    }

    @PostMapping("/{leadId}/upload-booking-form")
    public LeadResponseDTO uploadBookingForm(@PathVariable String leadId, @RequestParam("file") MultipartFile file) {
        LeadModel lead = leadService.getLeadByLeadId(leadId);
        String fileUrl = minioService.uploadDocumentsImg(file, leadId);
        lead.setBookingFormFileName(fileUrl);
        leadService.createLead(lead); // Save updated lead
        return LeadMapper.mapToResponseDTO(lead, pipelineStageService, employeeService);
    }

    // 📊 Lead Queries - Manager view (updated to use stageId)
    @GetMapping("/stage/{stageId}")
    public List<LeadResponseDTO> getLeadsByStageId(@PathVariable String stageId) {
        return leadService.getLeadsByStageId(stageId).stream()
            .map(lead -> LeadMapper.mapToResponseDTO(lead, pipelineStageService, employeeService))
            .toList();
    }

    // 📊 Conversion Stats - Employee-specific
    @GetMapping("/my/conversion-stats")
    public LeadService.ConversionStatsResponse getMyConversionStats(@RequestParam String employeeId) {
        return leadService.getConversionStatsForSales(employeeId);
    }

    // 📊 Conversion Stats - Manager view
    @GetMapping("/conversion-stats")
    public LeadService.ConversionStatsResponse getConversionStats() {
        return leadService.getConversionStats();
    }

    // 👥 Assignment Management
    @PatchMapping("/{leadId}/assign")
    public LeadResponseDTO updateLeadAssignment(@PathVariable String leadId, @Valid @RequestBody LeadAssignmentRequestDTO assignmentData) {
        return LeadMapper.mapToResponseDTO(leadService.updateLeadAssignment(leadId, assignmentData, "Public User"), pipelineStageService, employeeService);
    }

    @PostMapping("/{leadId}/activities/{activityId}/upload-attachment")
    public LeadResponseDTO uploadActivityAttachment(@PathVariable String leadId, @PathVariable String activityId, @RequestParam("file") MultipartFile file) {
        LeadModel lead = leadService.getLeadByLeadId(leadId);
        String fileUrl = minioService.uploadDocumentsImg(file, leadId);
        if (lead.getActivities() != null) {
            for (Activity activity : lead.getActivities()) {
                if (activityId.equals(activity.getId())) {
                    activity.setAttachment(fileUrl);
                    break;
                }
            }
        }
        leadService.createLead(lead); // Save updated lead
        return LeadMapper.mapToResponseDTO(lead, pipelineStageService, employeeService);
    }

    @GetMapping("/{leadId}/activities")
    public List<ActivityDTO> getLeadActivities(@PathVariable String leadId) {
        LeadModel lead = leadService.getLeadByLeadId(leadId);
        if (lead.getActivities() == null) {
            return java.util.Collections.emptyList();
        }
        return lead.getActivities().stream()
            .map(activity -> new ActivityDTO(
                activity.getId(),
                activity.getType(),
                activity.getTitle(),
                activity.getNotes(),
                activity.getDueDate(),
                activity.getDueTime(),
                activity.getUser(),
                activity.getStatus(),
                activity.getMeetingLink(),
                activity.getAttendees(),
                activity.getCallPurpose(),
                activity.getCallOutcome(),
                activity.getNextFollowUpDate(),
                activity.getNextFollowUpTime(),
                activity.getMeetingVenue(),
                activity.getNote(),
                activity.getAttachment()
            ))
            .toList();
    }

    @PostMapping("/{leadId}/activities/bulk")
    public LeadResponseDTO addActivitiesBulk(@PathVariable String leadId, @RequestBody List<ActivityDTO> activities) {
        return LeadMapper.mapToResponseDTO(leadService.addActivitiesBulk(leadId, activities, "Public User"), pipelineStageService, employeeService);
    }

    @PostMapping(path = "/{leadId}/activities/bulk-with-files", consumes = {"multipart/form-data"})
    public LeadResponseDTO addActivitiesBulkWithFiles(
        @PathVariable String leadId,
        @RequestParam("activities") String activitiesJson,
        @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<ActivityDTO> activities = objectMapper.readValue(
            activitiesJson, new com.fasterxml.jackson.core.type.TypeReference<List<ActivityDTO>>() {}
        );
        return LeadMapper.mapToResponseDTO(
            leadService.addActivitiesBulkWithFiles(leadId, activities, files, "Public User"),
            pipelineStageService, employeeService
        );
    }
}
