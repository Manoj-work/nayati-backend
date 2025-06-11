package com.medhir.rest.dto.attendance;

import lombok.Data;

@Data
public class DailyAttendanceDTO {
    private String employeeId;
    private String employeeName;
    private String attendanceStatus;
    private String date;
    private String month;
    private String year;
}