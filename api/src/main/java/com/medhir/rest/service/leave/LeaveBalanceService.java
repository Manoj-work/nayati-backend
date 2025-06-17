package com.medhir.rest.service.leave;

import com.medhir.rest.service.EmployeeService;
import com.medhir.rest.dto.EmployeeWithLeaveDetailsDTO;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.leave.LeaveBalance;
import com.medhir.rest.repository.leave.LeaveBalanceRepository;
import com.medhir.rest.service.settings.DepartmentService;
import com.medhir.rest.service.settings.LeaveTypeService;
import com.medhir.rest.service.settings.LeavePolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final LeavePolicyService leavePolicyService;
    private final LeaveTypeService leaveTypeService;

    private double calculateMonthlyLeaves(String employeeId) {
        // Get employee's department
        Optional<EmployeeWithLeaveDetailsDTO> employeeOpt = employeeService.getEmployeeById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }
        var employee = employeeOpt.get();

        // Get department's leave policy
        var department = departmentService.getDepartmentById(employee.getDepartment());
        var leavePolicy = leavePolicyService.getLeavePolicyById(department.getLeavePolicy());
        var leaveTypeId = leavePolicy.getLeaveAllocations().get(0).getLeaveTypeId();
        var leaveType = leaveTypeService.getLeaveTypeById(leaveTypeId).getLeaveTypeName();

        // Find the leave allocation for this leave type
        var leaveAllocation = leavePolicy.getLeaveAllocations().stream()
                .filter(allocation -> {
                    var leaveTypeModel = leaveTypeService.getLeaveTypeById(allocation.getLeaveTypeId());
                    return leaveTypeModel.getLeaveTypeName().equals(leaveType);
                })
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Leave type " + leaveType + " not found in policy"));

        // Get the leave type details for accrual period
        var leaveTypeModel = leaveTypeService.getLeaveTypeById(leaveAllocation.getLeaveTypeId());
        double daysPerYear = leaveAllocation.getDaysPerYear();

        // Calculate monthly leaves based on accrual period
        switch (leaveTypeModel.getAccrualPeriod().toUpperCase()) {
            case "MONTHLY":
                return daysPerYear / 12.0;
            case "QUARTERLY":
                return daysPerYear / 4.0;
            case "ANNUALLY":
                return daysPerYear;
            default:
                throw new IllegalArgumentException("Invalid accrual period: " + leaveTypeModel.getAccrualPeriod());
        }
    }

    public LeaveBalance getOrCreateLeaveBalance(String employeeId, String month, int year) {
        // Validate employee exists
        Optional<EmployeeWithLeaveDetailsDTO> employeeOpt = employeeService.getEmployeeById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }

        // Try to find existing balance
        Optional<LeaveBalance> existingBalance = leaveBalanceRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year);
        if (existingBalance.isPresent()) {
            return existingBalance.get();
        }

        // Create new balance
        LeaveBalance newBalance = new LeaveBalance();
        newBalance.setEmployeeId(employeeId);
        newBalance.setMonth(month);
        newBalance.setNumericMonth(Month.valueOf(month.toUpperCase()).getValue());
        newBalance.setYear(year);

        // Calculate earned leaves based on policy
        double earnedLeaves = calculateMonthlyLeaves(employeeId);
        newBalance.setAnnualLeavesEarned(earnedLeaves);
        newBalance.setCompOffLeavesEarned(0.0);
        newBalance.setLeavesTakenInThisMonth(0.0);

        // Get previous month's balance
        LeaveBalance previousBalance = getPreviousMonthBalance(employeeId, month, year);

        if (previousBalance == null) {
            // Case 1: No previous balance exists
            newBalance.setAnnualLeavesCarryForwarded(0.0);
            newBalance.setCompOffLeavesCarryForwarded(0.0);
            newBalance.setTotalAnnualLeavesEarnedSinceJanuary(earnedLeaves);
            newBalance.setTotalCompOffLeavesEarnedSinceJanuary(0.0);
            newBalance.setLeavesTakenThisYear(0.0);
            newBalance.setLeavesCarriedFromPreviousYear(0.0);
        } else {
            // Case 2: Previous balance exists - Calculate carry forward
            double totalCompOff = previousBalance.getCompOffLeavesEarned() + previousBalance.getCompOffLeavesCarryForwarded();
            double totalAnnual = previousBalance.getAnnualLeavesEarned() + previousBalance.getAnnualLeavesCarryForwarded();
            double leavesTaken = previousBalance.getLeavesTakenInThisMonth();

            // First deduct from comp-off
            if (leavesTaken <= totalCompOff) {
                // All leaves taken from comp-off
                newBalance.setCompOffLeavesCarryForwarded(totalCompOff - leavesTaken);
                newBalance.setAnnualLeavesCarryForwarded(totalAnnual);
            } else {
                // Comp-off fully used, remaining from annual
                newBalance.setCompOffLeavesCarryForwarded(0.0);
                newBalance.setAnnualLeavesCarryForwarded(totalAnnual - (leavesTaken - totalCompOff));
            }

            // Carry forward yearly totals
            newBalance.setTotalAnnualLeavesEarnedSinceJanuary(previousBalance.getTotalAnnualLeavesEarnedSinceJanuary() + earnedLeaves);
            newBalance.setTotalCompOffLeavesEarnedSinceJanuary(previousBalance.getTotalCompOffLeavesEarnedSinceJanuary());
            newBalance.setLeavesTakenThisYear(previousBalance.getLeavesTakenThisYear() + previousBalance.getLeavesTakenInThisMonth());

            // Handle leaves carried from previous year for January
            if (Month.valueOf(month.toUpperCase()) == Month.JANUARY) {
                Optional<LeaveBalance> decemberBalance = leaveBalanceRepository.findByEmployeeIdAndNumericMonthAndYear(
                        employeeId,
                        12,  // December
                        year - 1  // Previous year
                );

                if (decemberBalance.isPresent()) {
                    double decTotalCompOff = decemberBalance.get().getCompOffLeavesEarned() + decemberBalance.get().getCompOffLeavesCarryForwarded();
                    double decTotalAnnual = decemberBalance.get().getAnnualLeavesEarned() + decemberBalance.get().getAnnualLeavesCarryForwarded();
                    double decLeavesTaken = decemberBalance.get().getLeavesTakenInThisMonth();

                    // Calculate remaining leaves from December
                    double remainingCompOff = Math.max(0, decTotalCompOff - decLeavesTaken);
                    double remainingAnnual = decTotalAnnual - Math.max(0, decLeavesTaken - decTotalCompOff);

                    newBalance.setLeavesCarriedFromPreviousYear(remainingAnnual);
                } else {
                    newBalance.setLeavesCarriedFromPreviousYear(0.0);
                }
            } else {
                newBalance.setLeavesCarriedFromPreviousYear(previousBalance.getLeavesCarriedFromPreviousYear());
            }
        }

        return leaveBalanceRepository.save(newBalance);
    }

    private LeaveBalance getPreviousMonthBalance(String employeeId, String currentMonth, int year) {
        Month month = Month.valueOf(currentMonth.toUpperCase());
        int previousMonthValue = month.getValue() - 1;
        int previousYear = year;

        if (previousMonthValue == 0) {
            previousMonthValue = 12;
            previousYear--;
        }

        return leaveBalanceRepository.findByEmployeeIdAndNumericMonthAndYear(employeeId, previousMonthValue, previousYear)
                .orElse(null);
    }

    public LeaveBalance getCurrentMonthBalance(String employeeId) {
        LocalDate now = LocalDate.now();
        return getOrCreateLeaveBalance(employeeId, now.getMonth().toString(), now.getYear());
    }

} 