package com.medhir.rest.settings;

import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.company.CompanyModel;
import com.medhir.rest.model.settings.LeaveTypeModel;
import com.medhir.rest.repository.settings.LeaveTypeRepository;
import com.medhir.rest.service.company.CompanyService;
import com.medhir.rest.service.settings.LeaveTypeService;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LeaveTypeServiceTest {

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private CompanyService companyService;

    @Mock
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @InjectMocks
    private LeaveTypeService leaveTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------- CREATE LEAVE TYPE ------------

    @Test
    void createLeaveType_Success() {
        LeaveTypeModel leaveType = new LeaveTypeModel();
        leaveType.setCompanyId("COMP1");
        leaveType.setLeaveTypeName("Annual Leave");

        CompanyModel mockCompany = mock(CompanyModel.class);
        when(companyService.getCompanyById("COMP1")).thenReturn(Optional.of(mockCompany));
        when(leaveTypeRepository.existsByLeaveTypeName("Annual Leave")).thenReturn(false);
        when(snowflakeIdGenerator.nextId()).thenReturn(12345L);
        when(leaveTypeRepository.save(any(LeaveTypeModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveTypeModel created = leaveTypeService.createLeaveType(leaveType);

        assertNotNull(created.getLeaveTypeId());
        assertTrue(created.getLeaveTypeId().startsWith("LT"));
        verify(leaveTypeRepository).save(created);
    }

    @Test
    void createLeaveType_CompanyNotFound_ThrowsException() {
        LeaveTypeModel leaveType = new LeaveTypeModel();
        leaveType.setCompanyId("COMP1");

        when(companyService.getCompanyById("COMP1")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            leaveTypeService.createLeaveType(leaveType);
        });

        assertEquals("Company not found with id: COMP1", ex.getMessage());
        verify(leaveTypeRepository, never()).save(any());
    }

    @Test
    void createLeaveType_DuplicateName_ThrowsException() {
        LeaveTypeModel leaveType = new LeaveTypeModel();
        leaveType.setCompanyId("COMP1");
        leaveType.setLeaveTypeName("Annual Leave");

        CompanyModel mockCompany = mock(CompanyModel.class);
        when(companyService.getCompanyById("COMP1")).thenReturn(Optional.of(mockCompany));
        when(leaveTypeRepository.existsByLeaveTypeName("Annual Leave")).thenReturn(true);

        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class, () -> {
            leaveTypeService.createLeaveType(leaveType);
        });

        assertTrue(ex.getMessage().contains("already exists"));
        verify(leaveTypeRepository, never()).save(any());
    }

    // ----------- GET LEAVE TYPES ------------

    @Test
    void getAllLeaveTypes_ReturnsList() {
        LeaveTypeModel lt1 = new LeaveTypeModel();
        LeaveTypeModel lt2 = new LeaveTypeModel();
        when(leaveTypeRepository.findAll()).thenReturn(Arrays.asList(lt1, lt2));

        List<LeaveTypeModel> result = leaveTypeService.getAllLeaveTypes();

        assertEquals(2, result.size());
    }

    @Test
    void getLeaveTypesByCompanyId_ReturnsList() {
        LeaveTypeModel lt1 = new LeaveTypeModel();
        LeaveTypeModel lt2 = new LeaveTypeModel();
        when(leaveTypeRepository.findByCompanyId("COMP1")).thenReturn(Arrays.asList(lt1, lt2));

        List<LeaveTypeModel> result = leaveTypeService.getLeaveTypesByCompanyId("COMP1");

        assertEquals(2, result.size());
    }

    @Test
    void getLeaveTypeById_FoundByLeaveTypeId() {
        LeaveTypeModel leaveType = new LeaveTypeModel();
        leaveType.setLeaveTypeId("LT123");

        when(leaveTypeRepository.findByLeaveTypeId("LT123")).thenReturn(Optional.of(leaveType));

        LeaveTypeModel result = leaveTypeService.getLeaveTypeById("LT123");

        assertEquals(leaveType, result);
    }

    @Test
    void getLeaveTypeById_NotFound_ThrowsException() {
        when(leaveTypeRepository.findByLeaveTypeId("id")).thenReturn(Optional.empty());
        when(leaveTypeRepository.findById("id")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            leaveTypeService.getLeaveTypeById("id");
        });

        assertEquals("Leave type not found with id: id", ex.getMessage());
    }

    // ----------- UPDATE LEAVE TYPE ------------

    @Test
    void updateLeaveType_Success() {
        String id = "LT123";

        LeaveTypeModel existing = new LeaveTypeModel();
        existing.setLeaveTypeId(id);
        existing.setLeaveTypeName("Old Name");
        existing.setCompanyId("COMP1");

        LeaveTypeModel update = new LeaveTypeModel();
        update.setLeaveTypeName("New Name");
        update.setDescription("Updated Desc");
        update.setAccrualPeriod("MONTHLY");
        update.setAllowedInNoticePeriod(true);
        update.setAllowedInProbationPeriod(false);
        update.setCanBeCarriedForward(true);
        update.setCompanyId("COMP2");

        when(leaveTypeRepository.findByLeaveTypeId(id)).thenReturn(Optional.of(existing));
        CompanyModel mockCompany = mock(CompanyModel.class);
        when(companyService.getCompanyById("COMP2")).thenReturn(Optional.of(mockCompany));
        when(leaveTypeRepository.existsByLeaveTypeName("New Name")).thenReturn(false);
        when(leaveTypeRepository.save(existing)).thenReturn(existing);

        LeaveTypeModel result = leaveTypeService.updateLeaveType(id, update);

        assertEquals("New Name", result.getLeaveTypeName());
        assertEquals("Updated Desc", result.getDescription());
        assertEquals("MONTHLY", result.getAccrualPeriod());
        assertTrue(result.isAllowedInNoticePeriod());
        assertFalse(result.isAllowedInProbationPeriod());
        assertTrue(result.isCanBeCarriedForward());
        assertEquals("COMP2", result.getCompanyId());
    }

    @Test
    void updateLeaveType_LeaveTypeNotFound_ThrowsException() {
        String id = "LT999";
        LeaveTypeModel update = new LeaveTypeModel();
        when(leaveTypeRepository.findByLeaveTypeId(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            leaveTypeService.updateLeaveType(id, update);
        });

        assertEquals("Leave type not found with id: " + id, ex.getMessage());
        verify(leaveTypeRepository, never()).save(any());
    }

    @Test
    void updateLeaveType_CompanyNotFound_ThrowsException() {
        String id = "LT123";

        LeaveTypeModel existing = new LeaveTypeModel();
        existing.setLeaveTypeId(id);
        existing.setCompanyId("COMP1");

        LeaveTypeModel update = new LeaveTypeModel();
        update.setCompanyId("COMP2");

        when(leaveTypeRepository.findByLeaveTypeId(id)).thenReturn(Optional.of(existing));
        when(companyService.getCompanyById("COMP2")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            leaveTypeService.updateLeaveType(id, update);
        });

        assertEquals("Company not found with id: COMP2", ex.getMessage());
        verify(leaveTypeRepository, never()).save(any());
    }

    @Test
    void updateLeaveType_DuplicateName_ThrowsException() {
        String id = "LT123";

        LeaveTypeModel existing = new LeaveTypeModel();
        existing.setLeaveTypeId(id);
        existing.setLeaveTypeName("Old Name");

        LeaveTypeModel update = new LeaveTypeModel();
        update.setLeaveTypeName("Existing Name");

        when(leaveTypeRepository.findByLeaveTypeId(id)).thenReturn(Optional.of(existing));
        when(leaveTypeRepository.existsByLeaveTypeName("Existing Name")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            leaveTypeService.updateLeaveType(id, update);
        });

        assertEquals("Leave type with name Existing Name already exists", ex.getMessage());
        verify(leaveTypeRepository, never()).save(any());
    }

    // ----------- DELETE LEAVE TYPE ------------

    @Test
    void deleteLeaveType_Success() {
        LeaveTypeModel leaveType = new LeaveTypeModel();
        leaveType.setLeaveTypeId("LT123");
        leaveType.setId("mongoId123");  // MongoDB _id

        when(leaveTypeRepository.findByLeaveTypeId("LT123")).thenReturn(Optional.of(leaveType));

        leaveTypeService.deleteLeaveType("LT123");

        verify(leaveTypeRepository).deleteById("mongoId123");
    }

    @Test
    void deleteLeaveType_NotFound_ThrowsException() {
        when(leaveTypeRepository.findByLeaveTypeId("LT999")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            leaveTypeService.deleteLeaveType("LT999");
        });

        assertEquals("Leave type not found with id: LT999", ex.getMessage());
        verify(leaveTypeRepository, never()).deleteById(any());
    }

    // ----------- EXISTENCE CHECK ------------

    @Test
    void existsByLeaveTypeName_ReturnsTrue() {
        when(leaveTypeRepository.existsByLeaveTypeName("Annual Leave")).thenReturn(true);

        boolean exists = leaveTypeService.existsByLeaveTypeName("Annual Leave");

        assertTrue(exists);
    }

    @Test
    void existsByLeaveTypeName_ReturnsFalse() {
        when(leaveTypeRepository.existsByLeaveTypeName("Sick Leave")).thenReturn(false);

        boolean exists = leaveTypeService.existsByLeaveTypeName("Sick Leave");

        assertFalse(exists);
    }
}
