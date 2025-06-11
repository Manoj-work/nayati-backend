package com.medhir.rest.dto.leave;

import lombok.Data;

@Data
public class UpdateLeaveStatusRequest {
    private String leaveId;
    private String status; // "Approved" or "Rejected"
    private String remarks;
} 