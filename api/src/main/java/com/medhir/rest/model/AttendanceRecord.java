package com.medhir.rest.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document(collection = "attendance_records")
public class AttendanceRecord {
    @Id
    private String id;
    
    private String month;
    private String year;
    private String employeeId;
    private String employeeName;
    private List<String> weeklyHoliday;
    private int workingDays;
    private Map<String, String> dailyAttendance; // Map of date to attendance status
    private double payableDays;
    private double leavesTaken;
    private double leavesEarned;
    private double compOffEarned;
    private double lastMonthBalance;
    private double netLeaveBalance;
    private double payableLeaves;
    private double leavesPaid;
} 