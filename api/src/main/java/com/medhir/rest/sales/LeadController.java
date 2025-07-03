package com.medhir.rest.sales;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhir.rest.repository.EmployeeRepository;
import com.medhir.rest.sales.dto.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/sales/leads")
public class LeadController {

    @Autowired
    private LeadService leadService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;


    // ===================== Lead Endpoints =====================


@PostMapping
@ResponseStatus(HttpStatus.OK)
public LeadResponseDTO createLead(@Valid @RequestBody ModelLead lead) {
    ModelLead created = leadService.createLead(lead);
    return leadService.toLeadResponseDTO(created);
}



    @GetMapping
    public List<LeadResponseDTO> getAllLeads() {
        List<ModelLead> leads = leadService.getAllLeads();
        return leadService.toLeadResponseDTOList(leads);
    }



    @GetMapping("/{leadId}")
    public LeadResponseDTO getLeadById(@PathVariable String leadId) {
        ModelLead lead = leadService.getLeadByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead with ID '" + leadId + "' not found"));
        return leadService.toLeadResponseDTO(lead);
    }



@GetMapping("/employee/{employeeId}")
public ResponseEntity<List<LeadResponseDTO>> getLeadsByEmployeeId(@PathVariable String employeeId) {
    List<ModelLead> leads = leadService.getLeadsByEmployeeId(employeeId);
    List<LeadResponseDTO> dtos = leadService.toLeadResponseDTOList(leads);
    return ResponseEntity.ok(dtos);
}



    @PutMapping("/{leadId}")
    public ModelLead updateLead(@PathVariable String leadId, @RequestBody ModelLead lead) {
        return leadService.updateLead(leadId, lead);
    }

    // ==================== PATCH STATUS ENDPOINT ====================
    @PatchMapping("/{leadId}/stage")
    public ResponseEntity<MessageResponse> updateLeadStatus(
            @PathVariable String leadId,
            @RequestBody StatusUpdateRequest stageUpdateRequest) {

        leadService.updateLeadStatusByStageId(leadId, stageUpdateRequest.getStageId());
        return ResponseEntity.ok(new MessageResponse("Status updated successfully"));
    }



    @GetMapping("/stage/{stageId}")
    public List<LeadResponseDTO> getLeadsByStage(@PathVariable String stageId) {
        List<ModelLead> leads = leadService.getLeadsByStageId(stageId);
        return leadService.toLeadResponseDTOList(leads);
    }



    // ==================== Notes Management ===================
    @PostMapping("/{leadId}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public ModelLead.Note addNoteToLead(
            @PathVariable String leadId,
            @Valid @RequestBody NoteRequest noteRequest) {
        return leadService.addNoteToLead(leadId, noteRequest.getNote());
    }

    @GetMapping("/{leadId}/notes")
    public List<ModelLead.Note> getNotesForLead(@PathVariable String leadId) {
        return leadService.getNotesForLead(leadId);
    }

    // ===================== Activity Management Endpoints =====================

//    Add new activity to lead

    @PostMapping("/{leadId}/activities")
    public ResponseEntity<ModelLead.ActivityDetails> addActivity(
        @PathVariable String leadId,
        @ModelAttribute ActivityDetailsDto activityDetailsDto) {
    ModelLead.ActivityDetails created = leadService.addActivity(leadId, activityDetailsDto);
    return ResponseEntity.ok(created);
}



    @GetMapping("/{leadId}/activities")
    public List<ModelLead.ActivityDetails> getAllActivities(@PathVariable String leadId) {
        return leadService.getAllActivities(leadId);
    }


    @DeleteMapping("/{leadId}/activities/{activityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivity(
            @PathVariable String leadId,
            @PathVariable String activityId) {
        leadService.deleteActivity(leadId, activityId);
    }

    //====================Activity Log===============
    @GetMapping("/{leadId}/activity-log")
    public ResponseEntity<List<ModelLead.ActivityLogEntry>> getActivityLogForLead(@PathVariable String leadId) {
        List<ModelLead.ActivityLogEntry> logs = leadService.getActivityLogForLead(leadId);
        return ResponseEntity.ok(logs);
    }
//===================================================

    @PostMapping(value = "/{leadId}/activities/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ModelLead.ActivityDetails>> addActivities(
            @PathVariable String leadId,
            @RequestPart("activities") String activitiesJson,
            @RequestPart(value = "callAttachment", required = false) MultipartFile callAttachment,
            @RequestPart(value = "todoAttachment", required = false) MultipartFile todoAttachment,
            @RequestPart(value = "meetingAttachment", required = false) MultipartFile meetingAttachment,
            @RequestPart(value = "emailAttachment", required = false) MultipartFile emailAttachment
    ) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        List<ActivityDetailsDto> activities = mapper.readValue(activitiesJson, new TypeReference<List<ActivityDetailsDto>>() {});

        // Pass the files individually to the service
        List<ModelLead.ActivityDetails> created = leadService.addActivitiesWithFilesAndAttachments(
                leadId, activities,
                callAttachment, todoAttachment, meetingAttachment, emailAttachment);

        return ResponseEntity.ok(created);
    }
    @PutMapping(
            value = "/{leadId}/activities/{activityId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ModelLead.ActivityDetails> updateActivity(
            @PathVariable String leadId,
            @PathVariable String activityId,
            @RequestPart("activity") String activityJson,
            @RequestPart(value = "callAttachment", required = false) MultipartFile callAttachment,
            @RequestPart(value = "todoAttachment", required = false) MultipartFile todoAttachment,
            @RequestPart(value = "meetingAttachment", required = false) MultipartFile meetingAttachment,
            @RequestPart(value = "emailAttachment", required = false) MultipartFile emailAttachment
    ) throws Exception {
        ActivityDetailsDto dto = objectMapper.readValue(activityJson, ActivityDetailsDto.class);
        ModelLead.ActivityDetails updated = leadService.updateActivityWithTypeAttachment(
                leadId, activityId, dto, callAttachment, todoAttachment, meetingAttachment, emailAttachment
        );
        return ResponseEntity.ok(updated);
    }




}









