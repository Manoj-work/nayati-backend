package com.medhir.rest.sales;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhir.rest.sales.dto.ActivityDetailsDto;
import com.medhir.rest.sales.dto.NoteRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.medhir.rest.sales.dto.StatusUpdateRequest;
import com.medhir.rest.sales.dto.MessageResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/sales/leads")
public class LeadController {

    @Autowired
    private LeadService leadService;

    @Autowired
    private ObjectMapper objectMapper;


    // ===================== Lead Endpoints =====================

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ModelLead createLead(@Valid @RequestBody ModelLead lead) {
        return leadService.createLead(lead);
    }

//    @GetMapping("/manager/{managerId}")
//    public ResponseEntity<List<ModelLead>> getLeadsByManagerId(@PathVariable String managerId) {
//        List<ModelLead> leads = leadService.getAllLeads(managerId);
//        return ResponseEntity.ok(leads);
//    }
@GetMapping
public List<ModelLead> getAllLeads() {
    return leadService.getAllLeads();
}

    @GetMapping("/{leadId}")
    public ModelLead getLeadById(@PathVariable String leadId) {
        return leadService.getLeadByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead with ID '" + leadId + "' not found"));
    }
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ModelLead>> getLeadsByEmployeeId(@PathVariable String employeeId) {
        List<ModelLead> leads = leadService.getLeadsByEmployeeId(employeeId);
        return ResponseEntity.ok(leads);
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
    public List<ModelLead> getLeadsByStage(@PathVariable String stageId) {
        return leadService.getLeadsByStageId(stageId);
    }


    // ==================== Notes Management ===================
    @PostMapping("/{leadId}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public ModelLead.Note addNoteToLead(
            @PathVariable String leadId,
            @Valid @RequestBody NoteRequest noteRequest) {
        return leadService.addNoteToLead(leadId, noteRequest.getContent());
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



// Update activity
@PutMapping("/{leadId}/activities/{activityId}")
public ResponseEntity<ModelLead.ActivityDetails> updateActivity(
        @PathVariable String leadId,
        @PathVariable String activityId,
        @ModelAttribute ActivityDetailsDto activityDetailsDto) {
    ModelLead.ActivityDetails updated = leadService.updateActivity(leadId, activityId, activityDetailsDto);
    return ResponseEntity.ok(updated);
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


}









