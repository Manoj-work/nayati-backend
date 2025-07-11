
package com.medhir.rest.service.settings;

import com.medhir.rest.dto.CompanyDesignationDTO;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.settings.DepartmentModel;
import com.medhir.rest.model.settings.DesignationModel;
import com.medhir.rest.repository.settings.DesignationRepository;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DesignationServiceTest {

    @InjectMocks
    private DesignationService designationService;

    @Mock
    private DesignationRepository designationRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Mock
    private DepartmentService departmentService;

    private DesignationModel designation;
    private DepartmentModel department;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        designation = new DesignationModel();
        designation.setName("Manager");
        designation.setDepartment("D001");


        department = new DepartmentModel();
        department.setDepartmentId("D001");
        department.setName("HR");


    }

    @Test
    void Sucess_when_CreateDesignation() {


        when(designationRepository.existsByNameAndDepartment("Manager", "D001")).thenReturn(false);
        when(departmentService.getDepartmentById("D001")).thenReturn(department);
        when(snowflakeIdGenerator.nextId()).thenReturn(101L);

        when(designationRepository.save(any(DesignationModel.class))).thenAnswer(invocation -> {
            DesignationModel saved = invocation.getArgument(0);
            return saved;
          });

        DesignationModel result = designationService.createDesignation(designation);


        assertEquals("DES101", result.getDesignationId());
        assertEquals("Manager", result.getName());
        assertEquals("D001", result.getDepartment());



        verify(departmentService).getDepartmentById("D001");
        verify(designationRepository).save(any(DesignationModel.class));
    }


    @Test
    void test_CreateDesignation_DuplicateName_ThrowsException() {
        when(designationRepository.existsByNameAndDepartment("Manager", "D001")).thenReturn(true);

        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> designationService.createDesignation(designation));

        assertEquals("Designation with name Manager already exists in this department", ex.getMessage());
    }

    @Test
    void testGetDesignationById_FindsByDesignationId() {
        designation.setDesignationId("DES123");
        when(designationRepository.findByDesignationId("DES123")).thenReturn(Optional.of(designation));

        DesignationModel result = designationService.getDesignationById("DES123");
        assertAll(
                ()->assertEquals("DES123", result.getDesignationId()),
                ()-> assertEquals("Manager", result.getName()));

    }


    @Test
    void testGetDesignationById_NotFound_ThrowsResourcenotfoundException() {
        when(designationRepository.findByDesignationId("DES000")).thenReturn(Optional.empty());
        when(designationRepository.findById("DES000")).thenReturn(Optional.empty());


        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            designationService.getDesignationById("DES000");
        });

        assertEquals("Designation not found with id: DES000", ex.getMessage());
    }

    @Test
    void testGetAllDesignationsByCompanyId() {
        DesignationModel des = new DesignationModel();
        des.setDesignationId("DES001");
        des.setName("Lead");
        des.setDepartment("D001");
        des.setDescription("Senior Lead");
        des.setManager(true);
        des.setOvertimeEligible(true);

        when(departmentService.getDepartmentsByCompanyId("CID101")).thenReturn(List.of(department));
        when(designationRepository.findByDepartment("D001")).thenReturn(List.of(des));

        List<CompanyDesignationDTO> results = designationService.getAllDesignationsByCompanyId("CID101");

        assertEquals(1, results.size());
        CompanyDesignationDTO dto = results.get(0);
        assertEquals("Lead", dto.getName());
        assertEquals("Senior Lead", dto.getDescription());
        assertEquals("HR", dto.getDepartment());
        assertTrue(dto.isManager());
        assertTrue(dto.isOvertimeEligible());
    }

    @Test
    void Success_when_testUpdateDesignation() {
        DesignationModel existing = new DesignationModel();
        existing.setName("Manager");
        existing.setDepartment("D001");

        DesignationModel desigupdated = new DesignationModel();
        desigupdated.setName("newManager");
        desigupdated.setDepartment("D001");
        desigupdated.setDescription("Updated Description");
        desigupdated.setManager(true);
        desigupdated.setOvertimeEligible(true);

        when(designationRepository.findByDesignationId("DES101")).thenReturn(Optional.of(existing));
        when(designationRepository.existsByNameAndDepartment("newManager", "D001")).thenReturn(false);
        when(designationRepository.save(any(DesignationModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DesignationModel result = designationService.updateDesignation("DES101", desigupdated);
        assertEquals("newManager", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertTrue(result.isManager());
        assertTrue(result.isOvertimeEligible());
    }

//    @Test
//    void testDeleteDesignation() {
//        designation.setDesignationId("DES102");
//        when(designationRepository.findByDesignationId("DES102")).thenReturn(Optional.of(designation));
//
//        designationService.deleteDesignation("DES102");
//
//        verify(designationRepository, times(1)).delete(designation);
//    }
}
