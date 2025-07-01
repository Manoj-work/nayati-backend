package com.medhir.rest.sales;

import com.medhir.rest.sales.dto.NoteRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.medhir.rest.sales.dto.StatusUpdateRequest;
import com.medhir.rest.sales.dto.MessageResponse;

import java.util.List;

@RestController
@RequestMapping("/sales/leads")
public class LeadController {

    @Autowired
    private LeadService leadService;

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


//    @GetMapping("/status/{status}")
//    public List<ModelLead> getLeadsByStatus(@PathVariable String status) {
//        return leadService.getLeadsByStatus(status);
//    }
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

    // ===================== Kanban Status-Specific Endpoints =====================



    // ===================== Activity Management Endpoints =====================

    @PostMapping("/{leadId}/activities")
    @ResponseStatus(HttpStatus.CREATED)
    public ModelLead.ActivityDetails addActivity(
            @PathVariable String leadId,
            @Valid @RequestBody ModelLead.ActivityDetails activity) {
        return leadService.addOrUpdateActivity(leadId, activity);
    }

    @GetMapping("/{leadId}/activities")
    public List<ModelLead.ActivityDetails> getAllActivities(@PathVariable String leadId) {
        return leadService.getAllActivities(leadId);
    }


    @PutMapping("/{leadId}/activities/{activityId}")
    public ModelLead.ActivityDetails updateActivity(
            @PathVariable String leadId,
            @PathVariable String activityId,
            @Valid @RequestBody ModelLead.ActivityDetails activity) {
        return leadService.updateActivity(leadId, activityId, activity);
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









