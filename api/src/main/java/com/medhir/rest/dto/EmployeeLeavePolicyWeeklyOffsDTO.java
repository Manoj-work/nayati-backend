package com.medhir.rest.dto;

import com.medhir.rest.model.settings.LeavePolicyModel;
import lombok.Data;
import java.util.List;

@Data
public class EmployeeLeavePolicyWeeklyOffsDTO {
    private String employeeId;
    private String employeeName;
    private String leavePolicyId;
    private String leavePolicyName;
    private List<String> weeklyOffs;
    private List<LeavePolicyModel.LeaveAllocation> leaveAllocations;
} 