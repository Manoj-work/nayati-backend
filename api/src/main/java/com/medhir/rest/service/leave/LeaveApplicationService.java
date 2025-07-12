package com.medhir.rest.service.leave;

import com.medhir.rest.service.EmployeeService;
import com.medhir.rest.dto.EmployeeWithLeaveDetailsDTO;
import com.medhir.rest.dto.ManagerEmployeeDTO;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.dto.leave.LeaveWithEmployeeDetails;
import com.medhir.rest.dto.leave.UpdateLeaveStatusRequest;
import com.medhir.rest.model.leave.LeaveBalance;
import com.medhir.rest.model.leave.LeaveModel;
import com.medhir.rest.repository.leave.LeaveBalanceRepository;
import com.medhir.rest.repository.leave.LeaveRepository;
import com.medhir.rest.service.CompanyService;
import com.medhir.rest.service.settings.DepartmentService;
import com.medhir.rest.service.settings.LeaveTypeService;
import com.medhir.rest.service.settings.LeavePolicyService;
//import com.medhir.rest.utils.GeneratedId;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaveApplicationService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private LeavePolicyService leavePolicyService;

    @Autowired
    private LeaveTypeService leaveTypeService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    private final RestTemplate restTemplate = new RestTemplate();


    public LeaveModel applyLeave(LeaveModel request) {
        // Validate employee exists
        Optional<EmployeeWithLeaveDetailsDTO> employeeOpt = employeeService.getEmployeeById(request.getEmployeeId());
        if (employeeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found with ID: " + request.getEmployeeId());
        }

        // Validate company exists
        companyService.getCompanyById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + request.getCompanyId()));

        // Validate leave dates
        if (request.getLeaveDates() == null || request.getLeaveDates().isEmpty()) {
            throw new IllegalArgumentException("Leave dates cannot be empty");
        }

        // Create new leave object
        LeaveModel leave = new LeaveModel();

        // Copy all properties except leaveId and status
        BeanUtils.copyProperties(request, leave, "leaveId", "status");

        leave.setLeaveId("LID" + snowflakeIdGenerator.nextId());
        leave.setStatus("Pending");

        return leaveRepository.save(leave);
    }



    public LeaveModel updateLeaveStatus(UpdateLeaveStatusRequest request) {
        Optional<LeaveModel> leaveOpt = leaveRepository.findByLeaveId(request.getLeaveId());
        if (leaveOpt.isEmpty()) {
            throw new ResourceNotFoundException("Leave not found with ID: " + request.getLeaveId());
        }

        LeaveModel leave = leaveOpt.get();

        // Check if leave is already approved or rejected
        if ("Approved".equals(leave.getStatus()) || "Rejected".equals(leave.getStatus())) {
            throw new IllegalArgumentException("Leave is already " + leave.getStatus().toLowerCase());
        }

        if (!"Approved".equals(request.getStatus()) && !"Rejected".equals(request.getStatus())) {
            throw new IllegalArgumentException("Status must be either 'Approved' or 'Rejected'");
        }

        leave.setStatus(request.getStatus());
        leave.setRemarks(request.getRemarks());

        if ("Approved".equals(request.getStatus())) {
            if ("Leave".equals(leave.getLeaveName())) {
                handleRegularLeaveApproval(leave);
            } else if ("Comp-Off".equals(leave.getLeaveName())) {
                handleCompOffApproval(leave);
            }
        }

        return leaveRepository.save(leave);
    }

   private void handleRegularLeaveApproval(LeaveModel leave) {
        // Calculate the number of days for this leave
        double leaveDays = calculateLeaveDays(leave);
        
        // Get current month's leave balance
        LeaveBalance currentBalance = leaveBalanceService.getCurrentMonthBalance(leave.getEmployeeId());
        
        // Update leaves taken
        currentBalance.setLeavesTakenInThisMonth(currentBalance.getLeavesTakenInThisMonth() + leaveDays);
        currentBalance.setLeavesTakenThisYear(currentBalance.getLeavesTakenThisYear() + leaveDays);
        
        // Save the updated balance
        leaveBalanceRepository.save(currentBalance);
    }

    private void handleCompOffApproval(LeaveModel leave) {
        // Calculate the number of comp-off days
        double compOffDays = calculateLeaveDays(leave);
        
        // Get current month's leave balance
        LeaveBalance currentBalance = leaveBalanceService.getCurrentMonthBalance(leave.getEmployeeId());
        
        // Update comp-off earned
        currentBalance.setCompOffLeavesEarned(currentBalance.getCompOffLeavesEarned() + compOffDays);
        currentBalance.setTotalCompOffLeavesEarnedSinceJanuary(
            currentBalance.getTotalCompOffLeavesEarnedSinceJanuary() + compOffDays
        );
        
        // Save the updated balance
        leaveBalanceRepository.save(currentBalance);
    }

    private double calculateLeaveDays(LeaveModel leave) {
        double totalDays = leave.getLeaveDates().size();
        String shiftType = leave.getShiftType();
        
        if ("FULL_DAY".equals(shiftType)) {
            return totalDays;
        } else if ("FIRST_HALF".equals(shiftType) || "SECOND_HALF".equals(shiftType)) {
            return totalDays * 0.5;
        } else {
            return totalDays;
        }
    }
// controller .. hit api..
    public LeaveModel getLeaveByLeaveId(String leaveId) {
        return leaveRepository.findByLeaveId(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with ID: " + leaveId));
    }

    public List<LeaveWithEmployeeDetails> getLeavesByStatus(String companyId, String status) {
        // Validate company exists
        companyService.getCompanyById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        if (!"Pending".equals(status) && !"Approved".equals(status) && !"Rejected".equals(status)) {
            throw new IllegalArgumentException("Status must be either 'Pending', 'Approved', or 'Rejected'");
        }

        List<LeaveModel> leaves = leaveRepository.findByCompanyIdAndStatus(companyId, status);

        return leaves.stream().map(leave -> {
            LeaveWithEmployeeDetails leaveWithDetails = new LeaveWithEmployeeDetails();

            // Copy all fields from LeaveModel to LeaveWithEmployeeDetails
            leaveWithDetails.setId(leave.getId());
            leaveWithDetails.setLeaveDates(leave.getLeaveDates());
            leaveWithDetails.setLeaveId(leave.getLeaveId());
            leaveWithDetails.setEmployeeId(leave.getEmployeeId());
            leaveWithDetails.setCompanyId(leave.getCompanyId());
            leaveWithDetails.setLeaveName(leave.getLeaveName());
//            leaveWithDetails.setLeaveType(leave.getLeaveType());
            leaveWithDetails.setShiftType(leave.getShiftType());
            leaveWithDetails.setReason(leave.getReason());
            leaveWithDetails.setStatus(leave.getStatus());
            leaveWithDetails.setRemarks(leave.getRemarks());
            leaveWithDetails.setCreatedAt(leave.getCreatedAt());

            // Get employee details
            Optional<EmployeeWithLeaveDetailsDTO> employeeOpt = employeeService.getEmployeeById(leave.getEmployeeId());
            if (employeeOpt.isPresent()) {
                EmployeeWithLeaveDetailsDTO employee = employeeOpt.get();
                leaveWithDetails.setEmployeeName(employee.getName());

                // Get department name
                try {
                    if (employee.getDepartment() != null && !employee.getDepartment().isEmpty()) {
                        leaveWithDetails.setDepartment(departmentService.getDepartmentById(employee.getDepartment()).getName());
                    }
                } catch (Exception e) {
                    leaveWithDetails.setDepartment(employee.getDepartment());
                }
            }

            return leaveWithDetails;
        }).collect(Collectors.toList());
    }

    public List<LeaveModel> getLeavesByEmployeeId(String employeeId) {
        List<LeaveModel> leaves = leaveRepository.findByEmployeeId(employeeId);
        return leaves;
    }

    public List<LeaveWithEmployeeDetails> getLeavesByManagerIdAndStatus(String managerId, String status) {
        if (!"Pending".equals(status) && !"Approved".equals(status) && !"Rejected".equals(status)) {
            throw new IllegalArgumentException("Status must be either 'Pending', 'Approved', or 'Rejected'");
        }

        // Get all employees reporting to this manager
        List<ManagerEmployeeDTO> teamMembers = employeeService.getEmployeesByManager(managerId);
        List<String> teamMemberIds = teamMembers.stream()
                .map(ManagerEmployeeDTO::getEmployeeId)
                .collect(Collectors.toList());

        // Get all leaves for team members with the specified status
        List<LeaveModel> leaves = leaveRepository.findByEmployeeIdInAndStatus(teamMemberIds, status);

        return leaves.stream().map(leave -> {
            LeaveWithEmployeeDetails leaveWithDetails = new LeaveWithEmployeeDetails();

            // Copy all fields from LeaveModel to LeaveWithEmployeeDetails
            leaveWithDetails.setId(leave.getId());
            leaveWithDetails.setLeaveId(leave.getLeaveId());
            leaveWithDetails.setEmployeeId(leave.getEmployeeId());
            leaveWithDetails.setCompanyId(leave.getCompanyId());
            leaveWithDetails.setLeaveName(leave.getLeaveName());
            leaveWithDetails.setLeaveDates(leave.getLeaveDates());
//            leaveWithDetails.setLeaveType(leave.getLeaveType());
            leaveWithDetails.setShiftType(leave.getShiftType());
            leaveWithDetails.setReason(leave.getReason());
            leaveWithDetails.setStatus(leave.getStatus());
            leaveWithDetails.setRemarks(leave.getRemarks());
            leaveWithDetails.setCreatedAt(leave.getCreatedAt());

            // Get employee details
            Optional<EmployeeWithLeaveDetailsDTO> employeeOpt = employeeService.getEmployeeById(leave.getEmployeeId());
            if (employeeOpt.isPresent()) {
                EmployeeWithLeaveDetailsDTO employee = employeeOpt.get();
                leaveWithDetails.setEmployeeName(employee.getName());

                // Get department name
                try {
                    if (employee.getDepartment() != null && !employee.getDepartment().isEmpty()) {
                        leaveWithDetails.setDepartment(departmentService.getDepartmentById(employee.getDepartment()).getName());
                    }
                } catch (Exception e) {
                    leaveWithDetails.setDepartment(employee.getDepartment());
                }
            }

            return leaveWithDetails;
        }).collect(Collectors.toList());
    }
}