package com.medhir.rest.service.leave;

import com.medhir.rest.dto.EmployeeWithLeaveDetailsDTO;
import com.medhir.rest.exception.ResourceNotFoundException;
//import com.medhir.rest.model.leave.LeaveAllocation;
import com.medhir.rest.model.leave.LeaveBalance;
import com.medhir.rest.model.settings.DepartmentModel;
import com.medhir.rest.model.settings.LeavePolicyModel;
import com.medhir.rest.model.settings.LeaveTypeModel;
import com.medhir.rest.repository.leave.LeaveBalanceRepository;
import com.medhir.rest.service.employee.EmployeeService;
import com.medhir.rest.service.settings.DepartmentService;
import com.medhir.rest.service.settings.LeavePolicyService;
import com.medhir.rest.service.settings.LeaveTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LeaveBalanceServiceTest {

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private LeavePolicyService leavePolicyService;

    @Mock
    private LeaveTypeService leaveTypeService;

    @InjectMocks
    private LeaveBalanceService leaveBalanceService;

    private EmployeeWithLeaveDetailsDTO employee;
    private DepartmentModel department;
    private LeavePolicyModel leavePolicy;
    private LeavePolicyModel.LeaveAllocation leaveAllocation;
    private LeaveTypeModel leaveType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = new EmployeeWithLeaveDetailsDTO();
        employee.setEmployeeId("EMP001");
        employee.setDepartment("DEPT001");

        department = new DepartmentModel();
        department.setDepartmentId("DEPT001");
        department.setLeavePolicy("POLICY001");

        leaveAllocation = new LeavePolicyModel.LeaveAllocation();
        leaveAllocation.setLeaveTypeId("LT001");
        leaveAllocation.setDaysPerYear(12);

        leavePolicy = new LeavePolicyModel();
        leavePolicy.setLeavePolicyId("POLICY001");
        leavePolicy.setLeaveAllocations(List.of(leaveAllocation));

        leaveType = new LeaveTypeModel();
        leaveType.setLeaveTypeId("LT001");
        leaveType.setLeaveTypeName("Annual Leave");
        leaveType.setAccrualPeriod("MONTHLY");
    }

    @Test
    void successfully_calculateMonthlyLeaves() {
        // Arrange - Prepare mocks

        // Mock Employee
        EmployeeWithLeaveDetailsDTO employee = new EmployeeWithLeaveDetailsDTO();
        employee.setEmployeeId("EMP001");
        employee.setDepartment("DEPT001");
        when(employeeService.getEmployeeById("EMP001")).thenReturn(Optional.of(employee));

        // Mock Department
        DepartmentModel department = new DepartmentModel();
        department.setDepartmentId("DEPT001");
        department.setLeavePolicy("POLICY001");
        when(departmentService.getDepartmentById("DEPT001")).thenReturn(department);

        // Mock Leave Policy with Leave Allocation
        // Mock Leave Policy with Leave Allocation
        LeavePolicyModel leavePolicy = new LeavePolicyModel();
        LeavePolicyModel.LeaveAllocation leaveAllocation = new LeavePolicyModel.LeaveAllocation();
        leaveAllocation.setLeaveTypeId("LT001");
        leaveAllocation.setDaysPerYear(12);  // Example: 12 days per year

        leavePolicy.setLeaveAllocations(List.of(leaveAllocation));
        when(leavePolicyService.getLeavePolicyById("POLICY001")).thenReturn(leavePolicy);

        when(leavePolicyService.getLeavePolicyById("POLICY001")).thenReturn(leavePolicy);

        // Mock Leave Type (called multiple times inside stream & after)
        LeaveTypeModel leaveType = new LeaveTypeModel();
        leaveType.setLeaveTypeId("LT001");
        leaveType.setLeaveTypeName("Annual Leave");
        leaveType.setAccrualPeriod("MONTHLY");  // Monthly accrual
        when(leaveTypeService.getLeaveTypeById("LT001")).thenReturn(leaveType);

        // Mock repository - No previous record, so create new
        when(leaveBalanceRepository.findByEmployeeIdAndMonthAndYear("EMP001", "JANUARY", 2024))
                .thenReturn(Optional.empty());

        when(leaveBalanceRepository.findByEmployeeIdAndNumericMonthAndYear("EMP001", 12, 2023))
                .thenReturn(Optional.empty());

        when(leaveBalanceRepository.save(any(LeaveBalance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));  // Return the saved balance

        // Act
        LeaveBalance balance = leaveBalanceService.getOrCreateLeaveBalance("EMP001", "JANUARY", 2024);

        // Assert
        assertAll(
                ()->  assertEquals(1.0, balance.getAnnualLeavesEarned()), // 12 days per year / 12 months = 1.0 per month
        ()->  assertEquals("EMP001", balance.getEmployeeId()),
        ()-> assertEquals("JANUARY", balance.getMonth()),
        ()->   assertEquals(1, balance.getNumericMonth()),
        ()->    assertEquals(2024, balance.getYear())
        );


        // Verify repository interaction
        verify(leaveBalanceRepository).save(any(LeaveBalance.class));
    }


    @Test
    void shouldThrowResourceNotFoundWhenEmployeeMissing() {
        when(employeeService.getEmployeeById("EMP001")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            leaveBalanceService.getOrCreateLeaveBalance("EMP001", "JANUARY", 2024);
        });

        assertTrue(ex.getMessage().contains("Employee not found"));
    }

    @Test
    void shouldUseExistingBalanceIfPresent() {
        LeaveBalance existingBalance = new LeaveBalance();
        existingBalance.setEmployeeId("EMP001");
        existingBalance.setMonth("JANUARY");
        existingBalance.setYear(2024);

        when(employeeService.getEmployeeById("EMP001")).thenReturn(Optional.of(employee));
        when(leaveBalanceRepository.findByEmployeeIdAndMonthAndYear("EMP001", "JANUARY", 2024)).thenReturn(Optional.of(existingBalance));

        LeaveBalance result = leaveBalanceService.getOrCreateLeaveBalance("EMP001", "JANUARY", 2024);

        assertSame(existingBalance, result);
    }
}
