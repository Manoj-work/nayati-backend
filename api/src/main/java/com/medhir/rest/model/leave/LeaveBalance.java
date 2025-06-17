package com.medhir.rest.model.leave;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "LeaveBalances")
public class LeaveBalance {
    @Id
    @JsonIgnore
    private String id;
    private String employeeId;
    private String month;
    private int numericMonth; // For sorting (1-12)
    private int year;

    // Monthly earned leaves
    private double annualLeavesEarned;
    private double compOffLeavesEarned;

    // Carry forward from previous month
    private double annualLeavesCarryForwarded;
    private double compOffLeavesCarryForwarded;

    // Leaves taken in current month
    private double leavesTakenInThisMonth;

    // Yearly totals (since January)
    private double totalAnnualLeavesEarnedSinceJanuary;
    private double totalCompOffLeavesEarnedSinceJanuary;
    private double leavesTakenThisYear;
    private double leavesCarriedFromPreviousYear;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    private LocalDateTime updatedAt;

    // Computed field for total available balance
    public double getTotalAvailableBalance() {
        double totalEarned = annualLeavesEarned + compOffLeavesEarned;
        double totalCarryForwarded = annualLeavesCarryForwarded + compOffLeavesCarryForwarded;
        return totalEarned + totalCarryForwarded - leavesTakenInThisMonth;
    }
} 