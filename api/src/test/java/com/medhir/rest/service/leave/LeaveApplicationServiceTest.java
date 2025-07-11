package com.medhir.rest.service.leave;

import com.medhir.rest.dto.EmployeeWithLeaveDetailsDTO;
import com.medhir.rest.dto.ManagerEmployeeDTO;
import com.medhir.rest.dto.leave.LeaveWithEmployeeDetails;
import com.medhir.rest.dto.leave.UpdateLeaveStatusRequest;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.CompanyModel;
import com.medhir.rest.model.leave.LeaveBalance;
import com.medhir.rest.model.leave.LeaveModel;
import com.medhir.rest.model.settings.DepartmentModel;
import com.medhir.rest.repository.leave.LeaveBalanceRepository;
import com.medhir.rest.repository.leave.LeaveRepository;
import com.medhir.rest.service.CompanyService;
import com.medhir.rest.service.EmployeeService;
import com.medhir.rest.service.settings.DepartmentService;
import com.medhir.rest.service.settings.LeavePolicyService;
import com.medhir.rest.service.settings.LeaveTypeService;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaveApplicationServiceTest {

  @Mock
  private DepartmentService departmentService;

  private DepartmentModel departmentModel;
    @Mock
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private CompanyService companyService;

    @Mock
    private LeaveBalanceService leaveBalanceService;


    @Mock
    private LeavePolicyService leavePolicyService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private LeaveTypeService leaveTypeService;

    private LeaveModel mockleave;
   @Mock
   private LeaveBalance leaveBalance;
    public CompanyModel company;
    @Spy
    @InjectMocks
    private LeaveApplicationService leaveApplicationService;
    @Mock
    private EmployeeWithLeaveDetailsDTO employeeDTO;

    @BeforeEach
    public void setup() {
        mockleave = new LeaveModel();
        // Arrange: Complete test data
        mockleave.setEmployeeId("EMP123");
        mockleave.setCompanyId("COMP456");
        mockleave.setLeaveDates(List.of(LocalDate.now()));

        employeeDTO = new EmployeeWithLeaveDetailsDTO();
        employeeDTO.setCompanyId("COMP456");
        employeeDTO.setEmployeeId("EMP123");
//        employeeDTO.setName("John Doe");


        // companyModel.setCompanyId("COMP456");
        company = new CompanyModel();
        company.setCompanyId("COMP456");

    }

    @Test
    void Successwhen_Applyleave() {

        when(employeeService.getEmployeeById(mockleave.getEmployeeId())).thenReturn(Optional.of(employeeDTO));

        when(companyService.getCompanyById(mockleave.getCompanyId())).thenReturn(Optional.of(company));

        when(snowflakeIdGenerator.nextId()).thenReturn(123L);
        when(leaveRepository.save(any(LeaveModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        LeaveModel response = leaveApplicationService.applyLeave(mockleave);

        // Assert
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals("Pending", response.getStatus()),
                () -> assertEquals("COMP456", response.getCompanyId()),
                () -> assertTrue(response.getLeaveId().startsWith("LID")));

        verify(leaveRepository, times(1)).save(any(LeaveModel.class));
    }

    @Test
    void Throwsexception_WhenEmployeeNotFound_duringleaveapply() {
        when(employeeService.getEmployeeById(mockleave.getEmployeeId())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            leaveApplicationService.applyLeave(mockleave);
        });

        assertEquals("Employee not found with ID: EMP123", ex.getMessage());
    }

    @Test
    void applyLeave_ThrowsWhenCompanyNotFound() {

        when(employeeService.getEmployeeById(mockleave.getEmployeeId())).thenReturn(Optional.of(employeeDTO));

        when(companyService.getCompanyById(mockleave.getCompanyId())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            leaveApplicationService.applyLeave(mockleave);
        });

        assertEquals("Company not found with ID: COMP456", ex.getMessage());
    }

    @Test
    void applyLeave_ThrowsWhenLeaveDatesEmpty() {
        when(employeeService.getEmployeeById(mockleave.getEmployeeId())).thenReturn(Optional.of(employeeDTO));
        when(companyService.getCompanyById(mockleave.getCompanyId())).thenReturn(Optional.of(company));

        mockleave.setLeaveDates(List.of()); // Empty leave dates

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            leaveApplicationService.applyLeave(mockleave);
        });

        assertEquals("Leave dates cannot be empty", ex.getMessage());
    }



    @Test
    void updateleave_ThrowResourceNotFoundExceptionWhenLeaveNotFound() {

        UpdateLeaveStatusRequest request = new UpdateLeaveStatusRequest();
        request.setLeaveId("LID100");
//        request.setStatus("Approved");
       when(leaveRepository.findByLeaveId(request.getLeaveId())).thenReturn(Optional.empty());
//        when(leaveRepository.findByLeaveId("LID100")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            leaveApplicationService.updateLeaveStatus(request);
        });

        assertEquals("Leave not found with ID: LID100", ex.getMessage());
    }
    @Test
    void updateleave_shouldThrowExceptionWhenLeaveAlreadyApproved() {

        LeaveModel leave = new LeaveModel();
        leave.setLeaveId("LID101");
        leave.setStatus("Approved");

        UpdateLeaveStatusRequest request = new UpdateLeaveStatusRequest();
        request.setLeaveId("LID101");
        request.setStatus("Rejected");

        when(leaveRepository.findByLeaveId(leave.getLeaveId())).thenReturn(Optional.of(leave));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            leaveApplicationService.updateLeaveStatus(request);
        });

        assertEquals("Leave is already approved", ex.getMessage());
    }



    @Test
    void updateleave_shouldThrowExceptionWhenLeaveAlreadyRejected(){
        LeaveModel leave= new LeaveModel();
        leave.setLeaveId("LID101");
        leave.setStatus("Rejected");

        UpdateLeaveStatusRequest request=new UpdateLeaveStatusRequest();
        request.setLeaveId("LID101");
     //   request.setStatus("Approved");
        when(leaveRepository.findByLeaveId(leave.getLeaveId())).thenReturn(Optional.of(leave));

        IllegalArgumentException ex=assertThrows(IllegalArgumentException.class,()->{

            leaveApplicationService.updateLeaveStatus(request);

           });
        assertEquals("Leave is already rejected",ex.getMessage());
    }

@Test
void updateleave_shouldThrowExceptionWhenStatusIsInvalid() {

    LeaveModel leave = new LeaveModel();
    leave.setLeaveId("LID103");
    leave.setStatus("Pending");

    UpdateLeaveStatusRequest request = new UpdateLeaveStatusRequest();
    request.setLeaveId("LID103");
    request.setStatus("Processing"); // Invalid
    request.setRemarks("Invalid status update");

    when(leaveRepository.findByLeaveId("LID103")).thenReturn(Optional.of(leave));

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
        leaveApplicationService.updateLeaveStatus(request);
    });

    assertEquals("Status must be either 'Approved' or 'Rejected'", ex.getMessage());
}
    @Test
    void success_updateleave_shouldApproveRegularLeaveAndUpdateBalance() {
        // Arrange
        LeaveModel leave = new LeaveModel();
        leave.setLeaveId("LID101");
        leave.setEmployeeId("EMP001");
        leave.setStatus("Pending");
        leave.setLeaveName("Leave");
        leave.setShiftType("FULL_DAY");
        leave.setLeaveDates(List.of(LocalDate.now(), LocalDate.now().plusDays(1)));

        UpdateLeaveStatusRequest request = new UpdateLeaveStatusRequest();
        request.setLeaveId("LID101");
        request.setStatus("Approved");
        request.setRemarks("Approved by Manager");

        LeaveBalance balance = new LeaveBalance();
        balance.setLeavesTakenInThisMonth(2);
        balance.setLeavesTakenThisYear(5);

        when(leaveRepository.findByLeaveId("LID101")).thenReturn(Optional.of(leave));
        when(leaveBalanceService.getCurrentMonthBalance("EMP001")).thenReturn(balance);
        when(leaveRepository.save(any(LeaveModel.class))).thenAnswer(i -> i.getArgument(0));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(balance);

        // Act
        LeaveModel updatedLeave = leaveApplicationService.updateLeaveStatus(request);

        // Assert
        assertAll(
                ()-> assertEquals("Approved", updatedLeave.getStatus()),
        ()->    assertEquals("Approved by Manager", updatedLeave.getRemarks()),
        ()->    assertEquals(4, balance.getLeavesTakenInThisMonth()),  // 2 existing + 2 new days
        ()->    assertEquals(7, balance.getLeavesTakenThisYear())     // 5 existing + 2 new days
        );


        verify(leaveRepository).save(leave);
        verify(leaveBalanceRepository).save(balance);
    }

    @Test
    void success_updateleave_shouldApproveCompOffLeaveAndUpdateCompOffBalance() {
        // Arrange
        LeaveModel leave = new LeaveModel();
        leave.setLeaveId("LID202");
        leave.setEmployeeId("EMP002");
        leave.setStatus("Pending");
        leave.setLeaveName("Comp-Off");
        leave.setShiftType("FULL_DAY");
        leave.setLeaveDates(List.of(LocalDate.now()));

        UpdateLeaveStatusRequest request = new UpdateLeaveStatusRequest();
        request.setLeaveId("LID202");
        request.setStatus("Approved");
        request.setRemarks("Comp-Off Approved");

        LeaveBalance balance = new LeaveBalance();
        balance.setCompOffLeavesEarned(1);
        balance.setTotalCompOffLeavesEarnedSinceJanuary(3);

        when(leaveRepository.findByLeaveId("LID202")).thenReturn(Optional.of(leave));
        when(leaveBalanceService.getCurrentMonthBalance("EMP002")).thenReturn(balance);
        when(leaveRepository.save(any(LeaveModel.class))).thenAnswer(i -> i.getArgument(0));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(balance);

        // Act
        LeaveModel updatedLeave = leaveApplicationService.updateLeaveStatus(request);

        // Assert
        assertAll(
                ()-> assertEquals("Approved", updatedLeave.getStatus()),
                ()->assertEquals("Comp-Off Approved", updatedLeave.getRemarks()),
                ()->assertEquals(2, balance.getCompOffLeavesEarned()),                  // 1 existing + 1 new
                ()->assertEquals(4, balance.getTotalCompOffLeavesEarnedSinceJanuary()) // 3 existing + 1 new
        );


        verify(leaveRepository).save(leave);
        verify(leaveBalanceRepository).save(balance);
    }

    @Test
    void getLeaveByLeaveId_ShouldReturnLeave() {
        LeaveModel leave = new LeaveModel();
        leave.setLeaveId("LID300");

        when(leaveRepository.findByLeaveId("LID300")).thenReturn(Optional.of(leave));

        LeaveModel result = leaveApplicationService.getLeaveByLeaveId("LID300");

        assertNotNull(result);
        assertEquals("LID300", result.getLeaveId());
    }

    @Test
    void getLeaveByLeaveId_ShouldThrowWhenNotFound() {
        when(leaveRepository.findByLeaveId("LID001")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            leaveApplicationService.getLeaveByLeaveId("LID001");
        });
    }

    @Test
    void getLeavesByStatus_ShouldReturnLeaveDetails() {
        DepartmentModel departmentModel = new DepartmentModel();
        departmentModel.setDepartmentId("D001");
        departmentModel.setName("HR");

        LeaveModel leave = new LeaveModel();
        leave.setLeaveId("LID001");
        leave.setCompanyId("COMP001");
        leave.setEmployeeId("EMP001");
        leave.setLeaveName("Leave");
        leave.setShiftType("FULL_DAY");
        leave.setStatus("Pending");

        CompanyModel company = new CompanyModel();
        company.setCompanyId("COMP001");

        when(companyService.getCompanyById("COMP001")).thenReturn(Optional.of(company));
        when(leaveRepository.findByCompanyIdAndStatus("COMP001", "Pending")).thenReturn(List.of(leave));

        EmployeeWithLeaveDetailsDTO employee = new EmployeeWithLeaveDetailsDTO();
        employee.setEmployeeId("EMP001");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setDepartment("D001");

        when(employeeService.getEmployeeById("EMP001")).thenReturn(Optional.of(employee));
        when(departmentService.getDepartmentById("D001")).thenReturn(departmentModel);

        // Act
        List<LeaveWithEmployeeDetails> result = leaveApplicationService.getLeavesByStatus("COMP001", "Pending");

        // Assert
        assertEquals(1, result.size());
//        assertEquals("John Doe", result.get(0).getEmployeeName());
        assertEquals("HR", result.get(0).getDepartment());
    }

    @Test
    void getLeavesByStatus_ShouldThrowForInvalidStatus() {
        CompanyModel company = new CompanyModel();
        company.setCompanyId("COMP001");

        when(companyService.getCompanyById("COMP001")).thenReturn(Optional.of(company));

        assertThrows(IllegalArgumentException.class, () -> {
            leaveApplicationService.getLeavesByStatus("COMP001", "Invalid");
        });
    }


    @Test
    void getLeavesByStatus_ShouldThrowWhenCompanyNotFound() {
        when(companyService.getCompanyById("COMP001")).thenReturn(Optional.empty());

        ResourceNotFoundException ex= assertThrows(ResourceNotFoundException.class, () -> {
            leaveApplicationService.getLeavesByStatus("COMP001", "Pending");
        });
        assertEquals("Company not found with id: " + "COMP001", ex.getMessage());
    }

    @Test
    void getLeavesByEmployeeId_shouldReturnAllLeaves() {
        // Arrange - Prepare mock leave list
        LeaveModel leave1 = new LeaveModel();
        leave1.setLeaveId("LID101");
        leave1.setEmployeeId("EMP101");

        LeaveModel leave2 = new LeaveModel();
        leave2.setLeaveId("LID102");
        leave2.setEmployeeId("EMP101");

        List<LeaveModel> mockLeaves = List.of(leave1, leave2);

        // Mock repository call
        when(leaveRepository.findByEmployeeId("EMP101")).thenReturn(mockLeaves);

        // Act - Call service method
        List<LeaveModel> result = leaveApplicationService.getLeavesByEmployeeId("EMP101");

        // Assert - Validate the response
        assertEquals(2, result.size());
        assertEquals("LID101", result.get(0).getLeaveId());
        assertEquals("LID102", result.get(1).getLeaveId());

        // Verify interaction
        verify(leaveRepository).findByEmployeeId("EMP101");
    }
    @Test
    void getLeavesByManagerIdAndStatus_shouldReturnLeaveDetails() {
        // Mock team members
        ManagerEmployeeDTO teamMember = new ManagerEmployeeDTO();
        teamMember.setEmployeeId("EMP101");

        when(employeeService.getEmployeesByManager("MGR001")).thenReturn(List.of(teamMember));

        // Mock leave details
        LeaveModel leave = new LeaveModel();
        leave.setId("DBID1");
        leave.setLeaveId("LID101");
        leave.setEmployeeId("EMP101");
        leave.setCompanyId("COMP001");
        leave.setLeaveName("Casual Leave");
        leave.setLeaveDates(List.of(LocalDate.now()));
        leave.setShiftType("FULL_DAY");
        leave.setReason("Personal");
        leave.setStatus("Pending");
        leave.setRemarks("NA");
        leave.setCreatedAt(LocalDateTime.now());

        when(leaveRepository.findByEmployeeIdInAndStatus(List.of("EMP101"), "Pending"))
                .thenReturn(List.of(leave));

        // Mock employee details
        EmployeeWithLeaveDetailsDTO employee = new EmployeeWithLeaveDetailsDTO();
        employee.setEmployeeId("EMP101");
        employee.setFirstName("John ");
        employee.setLastName("Doe");
        employee.setDepartment("D001");

        when(employeeService.getEmployeeById("EMP101")).thenReturn(Optional.of(employee));

        // Mock department details
        DepartmentModel department = new DepartmentModel();
        department.setDepartmentId("D001");
        department.setName("HR");

        when(departmentService.getDepartmentById("D001")).thenReturn(department);

        // Act
        List<LeaveWithEmployeeDetails> result = leaveApplicationService.getLeavesByManagerIdAndStatus("MGR001", "Pending");

        // Assert
        assertEquals(1, result.size());
        LeaveWithEmployeeDetails leaveDetails = result.get(0);
//        assertEquals("John Doe", leaveDetails.getEmployeeName());
        assertEquals("HR", leaveDetails.getDepartment());
        assertEquals("LID101", leaveDetails.getLeaveId());
        assertEquals("EMP101", leaveDetails.getEmployeeId());

        // Verify
        verify(employeeService).getEmployeesByManager("MGR001");
        verify(leaveRepository).findByEmployeeIdInAndStatus(List.of("EMP101"), "Pending");
        verify(employeeService).getEmployeeById("EMP101");
        verify(departmentService).getDepartmentById("D001");
    }

    @Test
    void getLeavesByManagerIdAndStatus_shouldThrowForInvalidStatus() {
          IllegalArgumentException ex= assertThrows(IllegalArgumentException.class, () -> {
            leaveApplicationService.getLeavesByManagerIdAndStatus("MGR001", "InvalidStatus");
        });

        assertEquals("Status must be either 'Pending', 'Approved', or 'Rejected'", ex.getMessage());
    }



}



