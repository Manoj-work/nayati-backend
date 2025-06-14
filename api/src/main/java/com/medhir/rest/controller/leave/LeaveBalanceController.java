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

    // @PostMapping("/leaves-taken/{employeeId}")
    // public ResponseEntity<?> updateLeavesTaken(
    // @PathVariable String employeeId,
    // @RequestParam double days) {
    // try {
    // leaveBalanceService.updateLeavesTaken(employeeId, days);
    // return ResponseEntity.ok(Map.of("message", "Leaves taken updated
    // successfully"));
    // } catch (Exception e) {
    // return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    // }
    // }

    // @PostMapping("/comp-off/{employeeId}")
    // public ResponseEntity<?> addCompOffEarned(
    // @PathVariable String employeeId,
    // @RequestParam double days) {
    // try {
    // leaveBalanceService.addCompOffEarned(employeeId, days);
    // return ResponseEntity.ok(Map.of("message", "Comp-off earned added
    // successfully"));
    // } catch (Exception e) {
    // return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    // }
    // }
}