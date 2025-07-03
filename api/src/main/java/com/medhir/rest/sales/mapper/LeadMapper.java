package com.medhir.rest.sales.mapper;

import com.medhir.rest.sales.model.LeadModel;
import com.medhir.rest.sales.dto.lead.LeadResponseDTO;
import com.medhir.rest.sales.dto.activity.ActivityDTO;
import com.medhir.rest.sales.dto.activity.NoteDTO;
import com.medhir.rest.sales.dto.activity.ActivityLogDTO;
import com.medhir.rest.sales.service.PipelineStageService;
import com.medhir.rest.service.EmployeeService;
import java.util.stream.Collectors;

public class LeadMapper {
    public static LeadResponseDTO mapToResponseDTO(LeadModel lead, PipelineStageService pipelineStageService, EmployeeService employeeService) {
        LeadResponseDTO response = new LeadResponseDTO();
        response.setLeadId(lead.getLeadId());
        response.setName(lead.getName());
        response.setContactNumber(lead.getContactNumber());
        response.setEmail(lead.getEmail());
        response.setProjectType(lead.getProjectType());
        response.setPropertyType(lead.getPropertyType());
        response.setAddress(lead.getAddress());
        response.setArea(lead.getArea());
        response.setBudget(lead.getBudget());
        response.setDesignStyle(lead.getDesignStyle());
        response.setLeadSource(lead.getLeadSource());
        response.setNotes(lead.getNotes());
        response.setStageId(lead.getStageId());
        // Get stage information for better API responses
        if (lead.getStageId() != null) {
            var stage = pipelineStageService.getStageByIdOptional(lead.getStageId());
            if (stage.isPresent()) {
                response.setStageName(stage.get().getName());
                response.setStageColor(stage.get().getColor());
            }
        }
        response.setRating(lead.getRating());
        response.setPriority(lead.getPriority());
        // Map employee information with details
        if (lead.getSalesRep() != null) {
            response.setSalesRep(lead.getSalesRep());
            var salesRepOpt = employeeService.getEmployeeById(lead.getSalesRep());
            if (salesRepOpt.isPresent()) {
                var salesRepDetails = salesRepOpt.get();
                response.setSalesRepName(salesRepDetails.getName());
            }
        }
        if (lead.getDesigner() != null) {
            response.setDesigner(lead.getDesigner());
            var designerOpt = employeeService.getEmployeeById(lead.getDesigner());
            if (designerOpt.isPresent()) {
                var designerDetails = designerOpt.get();
                response.setDesignerName(designerDetails.getName());
            }
        }
        if (lead.getSubmittedBy() != null) {
            response.setSubmittedBy(lead.getSubmittedBy());
            var submittedByOpt = employeeService.getEmployeeById(lead.getSubmittedBy());
            if (submittedByOpt.isPresent()) {
                var submittedByDetails = submittedByOpt.get();
                response.setSubmittedByName(submittedByDetails.getName());
            }
        }
        response.setCallDescription(lead.getCallDescription());
        response.setCallHistory(lead.getCallHistory());
        response.setNextCall(lead.getNextCall());
        response.setQuotedAmount(lead.getQuotedAmount());
        response.setFinalQuotation(lead.getFinalQuotation());
        response.setSignupAmount(lead.getSignupAmount());
        response.setPaymentDate(lead.getPaymentDate());
        response.setPaymentMode(lead.getPaymentMode());
        response.setPanNumber(lead.getPanNumber());
        response.setDiscount(lead.getDiscount());
        response.setReasonForLost(lead.getReasonForLost());
        response.setReasonForJunk(lead.getReasonForJunk());
        response.setPaymentDetailsFileName(lead.getPaymentDetailsFileName());
        response.setBookingFormFileName(lead.getBookingFormFileName());
        response.setInitialQuote(lead.getInitialQuote());
        response.setProjectTimeline(lead.getProjectTimeline());
        response.setDateOfCreation(lead.getDateOfCreation());
        // Map activities
        if (lead.getActivities() != null) {
            response.setActivities(
                lead.getActivities().stream()
                    .map(activity -> new ActivityDTO(
                        activity.getId(),
                        activity.getType(),
                        activity.getSummary(),
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
                    .collect(Collectors.toList())
            );
        }
        // Map notesList
        if (lead.getNotesList() != null) {
            response.setNotesList(
                lead.getNotesList().stream()
                    .map(note -> new NoteDTO(
                        note.getId(),
                        note.getContent(),
                        note.getUser(),
                        note.getTimestamp()
                    ))
                    .collect(Collectors.toList())
            );
        }
        // Map activityLogs
        if (lead.getActivityLogs() != null) {
            response.setActivityLogs(
                lead.getActivityLogs().stream()
                    .map(log -> new ActivityLogDTO(
                        log.getId(),
                        log.getAction(),
                        log.getDetails(),
                        log.getUser(),
                        log.getTimestamp(),
                        log.getActivityType(),
                        log.getMetadata()
                    ))
                    .collect(Collectors.toList())
            );
        }
        return response;
    }
} 