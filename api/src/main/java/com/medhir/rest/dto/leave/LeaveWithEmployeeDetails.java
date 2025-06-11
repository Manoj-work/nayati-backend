package com.medhir.rest.dto.leave;

import com.medhir.rest.model.leave.LeaveModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LeaveWithEmployeeDetails extends LeaveModel {
    private String employeeName;
    private String department;
}