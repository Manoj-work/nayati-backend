package com.medhir.rest.controller.leave;

import com.medhir.rest.model.leave.LeaveBalance;
import com.medhir.rest.service.leave.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/leave-balance")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LeaveBalanceController {

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @GetMapping("/current/{employeeId}")
    public ResponseEntity<?> getCurrentMonthBalance(@PathVariable String employeeId) {
        try {
            LeaveBalance balance = leaveBalanceService.getCurrentMonthBalance(employeeId);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{employeeId}/{month}/{year}")
    public ResponseEntity<?> getLeaveBalanceForMonth(
            @PathVariable String employeeId,
            @PathVariable String month,
            @PathVariable int year) {
        try {
            LeaveBalance balance = leaveBalanceService.getOrCreateLeaveBalance(employeeId, month, year);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}