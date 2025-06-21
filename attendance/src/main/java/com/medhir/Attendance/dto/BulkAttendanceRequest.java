package com.medhir.Attendance.dto;

import lombok.Data;
import java.util.List;

@Data
public class BulkAttendanceRequest {
    private String employeeId;
    private String status; // Leave, LOP, Absent
    private String leaveId; // ID of the leave if status is "Leave"
    private List<String> dates; // yyyy-MM-dd
}
