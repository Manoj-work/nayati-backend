//
//package com.medhir.rest.sales;
//
//import com.medhir.rest.sales.dto.ConvertedLeadRequest;
//import com.medhir.rest.sales.dto.LostLeadRequest;
//import com.medhir.rest.sales.dto.JunkLeadRequest;
//import com.medhir.rest.sales.dto.NoteRequest;
//import jakarta.validation.Valid;
//import lombok.Data;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/sales/leads")
////@CrossOrigin(origins = "*")
//public class LeadController {
//
//    @Autowired
//    private LeadService leadService;
//
//    // ===================== Lead Endpoints =====================
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public ModelLead createLead(@Valid @RequestBody ModelLead lead) {
//        return leadService.createLead(lead);
//    }
//
//    @GetMapping
//    public List<ModelLead> getAllLeads() {
//        return leadService.getAllLeads();
//    }
//
//    @GetMapping("/{leadId}")
//    public ModelLead getLeadById(@PathVariable String leadId) {
//        return leadService.getLeadByLeadId(leadId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead with ID '" + leadId + "' not found"));
//    }
//
//    @PutMapping("/{leadId}")
//    public ModelLead updateLead(@PathVariable String leadId, @RequestBody ModelLead lead) {
//        return leadService.updateLead(leadId, lead);
//    }
//
//    // ==================== PATCH STATUS ENDPOINT ====================
//    @PatchMapping("/{leadId}/status")
//    public ResponseEntity<MessageResponse> updateLeadStatus(
//            @PathVariable String leadId,
//            @RequestBody StatusUpdateRequest statusUpdateRequest) {
//        leadService.updateLeadStatus(leadId, statusUpdateRequest.getStatus());
//        return ResponseEntity.ok(new MessageResponse("Status updated successfully"));
//    }
//    // ==================== END PATCH STATUS ENDPOINT ====================
//
//    @GetMapping("/status/{status}")
//    public List<ModelLead> getLeadsByStatus(@PathVariable String status) {
//        return leadService.getLeadsByStatus(status);
//    }
//
//    // ==================== Notes Management ===================
//    @PostMapping("/{leadId}/notes")
//    @ResponseStatus(HttpStatus.CREATED)
//    public ModelLead.Note addNoteToLead(
//            @PathVariable String leadId,
//            @Valid @RequestBody NoteRequest noteRequest) {
//        return leadService.addNoteToLead(leadId, noteRequest.getContent());
//    }
//
//    // ===================== Kanban Status-Specific Endpoints =====================
//
//    /**
//     * PATCH /sales/leads/{leadId}/convert
//     * For "Converted" form with file uploads (multipart/form-data)
//     */
//    @PatchMapping(value = "/{leadId}/convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<MessageResponse> convertLead(
//            @PathVariable String leadId,
//            @ModelAttribute ConvertedLeadRequest request) {
//        leadService.convertLead(leadId, request);
//        return ResponseEntity.ok(new MessageResponse("Lead converted successfully"));
//    }
//
//    /**
//     * PATCH /sales/leads/{leadId}/lost
//     * For "Lost" form (JSON)
//     */
//    @PatchMapping("/{leadId}/lost")
//    public ResponseEntity<MessageResponse> markLeadAsLost(
//            @PathVariable String leadId,
//            @RequestBody LostLeadRequest request) {
//        leadService.markLeadAsLost(leadId, request);
//        return ResponseEntity.ok(new MessageResponse("Lead marked as lost"));
//    }
//
//    /**
//     * PATCH /sales/leads/{leadId}/junk
//     * For "Junk" form (JSON)
//     */
//    @PatchMapping("/{leadId}/junk")
//    public ResponseEntity<MessageResponse> markLeadAsJunk(
//            @PathVariable String leadId,
//            @RequestBody JunkLeadRequest request) {
//        leadService.markLeadAsJunk(leadId, request);
//        return ResponseEntity.ok(new MessageResponse("Lead marked as junk"));
//    }
//
//    // ===================== Activity Management Endpoints =====================
//
//    @PostMapping("/{leadId}")
//    @ResponseStatus(HttpStatus.CREATED)
//    public ModelLead.ActivityDetails addActivity(
//            @PathVariable String leadId,
//            @Valid @RequestBody ModelLead.ActivityDetails activity) {
//        return leadService.addActivity(leadId, activity);
//    }
//
//    @GetMapping("/{leadId}/activities")
//    public List<ModelLead.ActivityDetails> getAllActivities(@PathVariable String leadId) {
//        return leadService.getAllActivities(leadId);
//    }
//
//    @GetMapping("/{leadId}/{type}/{activityId}")
//    public ModelLead.ActivityDetails getActivity(
//            @PathVariable String leadId,
//            @PathVariable String type,
//            @PathVariable String activityId) {
//
//        return leadService.getActivity(leadId, type, activityId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND,
//                        "Activity with ID '" + activityId + "' not found in lead '" + leadId + "'"
//                ));
//    }
//
//    @PutMapping("/{leadId}/{type}/{activityId}")
//    public ModelLead.ActivityDetails updateActivity(
//            @PathVariable String leadId,
//            @PathVariable String type,
//            @PathVariable String activityId,
//            @Valid @RequestBody ModelLead.ActivityDetails activity) {
//
//        if (!type.equalsIgnoreCase(activity.getType())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity type in path and body do not match");
//        }
//
//        return leadService.updateActivity(leadId, type, activityId, activity);
//    }
//
//    @DeleteMapping("/{leadId}/{type}/{activityId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteActivity(
//            @PathVariable String leadId,
//            @PathVariable String type,
//            @PathVariable String activityId) {
//
//        leadService.deleteActivity(leadId, type, activityId);
//    }
//
//    // ==================== DTOs ====================
//    @Data
//    public static class StatusUpdateRequest {
//        private String status;
//    }
//
//    @Data
//    public static class MessageResponse {
//        private String message;
//        public MessageResponse(String message) { this.message = message; }
//    }
//    // ==================== END DTOs ====================
//}
package com.medhir.rest.sales;

import com.medhir.rest.sales.dto.ConvertedLeadRequest;
import com.medhir.rest.sales.dto.LostLeadRequest;
import com.medhir.rest.sales.dto.JunkLeadRequest;
import com.medhir.rest.sales.dto.NoteRequest;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/sales/leads")
public class LeadController {

    @Autowired
    private LeadService leadService;

    // ===================== Lead Endpoints =====================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ModelLead createLead(@Valid @RequestBody ModelLead lead) {
        return leadService.createLead(lead);
    }

    @GetMapping
    public List<ModelLead> getAllLeads() {
        return leadService.getAllLeads();
    }

    @GetMapping("/{leadId}")
    public ModelLead getLeadById(@PathVariable String leadId) {
        return leadService.getLeadByLeadId(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead with ID '" + leadId + "' not found"));
    }

    @PutMapping("/{leadId}")
    public ModelLead updateLead(@PathVariable String leadId, @RequestBody ModelLead lead) {
        return leadService.updateLead(leadId, lead);
    }

    // ==================== PATCH STATUS ENDPOINT ====================
    @PatchMapping("/{leadId}/status")
    public ResponseEntity<MessageResponse> updateLeadStatus(
            @PathVariable String leadId,
            @RequestBody StatusUpdateRequest statusUpdateRequest) {
        leadService.updateLeadStatus(leadId, statusUpdateRequest.getStatus());
        return ResponseEntity.ok(new MessageResponse("Status updated successfully"));
    }

    @GetMapping("/status/{status}")
    public List<ModelLead> getLeadsByStatus(@PathVariable String status) {
        return leadService.getLeadsByStatus(status);
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

    @PatchMapping(value = "/{leadId}/convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> convertLead(
            @PathVariable String leadId,
            @ModelAttribute ConvertedLeadRequest request) {
        leadService.convertLead(leadId, request);
        return ResponseEntity.ok(new MessageResponse("Lead converted successfully"));
    }

    @PatchMapping("/{leadId}/lost")
    public ResponseEntity<MessageResponse> markLeadAsLost(
            @PathVariable String leadId,
            @RequestBody LostLeadRequest request) {
        leadService.markLeadAsLost(leadId, request);
        return ResponseEntity.ok(new MessageResponse("Lead marked as lost"));
    }

    @PatchMapping("/{leadId}/junk")
    public ResponseEntity<MessageResponse> markLeadAsJunk(
            @PathVariable String leadId,
            @RequestBody JunkLeadRequest request) {
        leadService.markLeadAsJunk(leadId, request);
        return ResponseEntity.ok(new MessageResponse("Lead marked as junk"));
    }

    // ===================== Activity Management Endpoints =====================

    @PostMapping("/{leadId}/activities")
    @ResponseStatus(HttpStatus.CREATED)
    public ModelLead.ActivityDetails addActivity(
            @PathVariable String leadId,
            @Valid @RequestBody ModelLead.ActivityDetails activity) {
        return leadService.addOrUpdateActivity(leadId, activity, null);
    }

    @GetMapping("/{leadId}/activities")
    public List<ModelLead.ActivityDetails> getAllActivities(@PathVariable String leadId) {
        return leadService.getAllActivities(leadId);
    }

    @GetMapping("/{leadId}/activities/{activityId}")
    public ModelLead.ActivityDetails getActivity(
            @PathVariable String leadId,
            @PathVariable String activityId) {
        return leadService.getActivityById(leadId, activityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Activity with ID '" + activityId + "' not found in lead '" + leadId + "'"
                ));
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

    // ==================== DTOs ====================
    @Data
    public static class StatusUpdateRequest {
        private String status;
    }

    @Data
    public static class MessageResponse {
        private String message;
        public MessageResponse(String message) { this.message = message; }
    }
}
