package com.medhir.rest.service.settings;

import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.company.CompanyModel;
import com.medhir.rest.model.settings.DepartmentModel;
import com.medhir.rest.model.settings.LeavePolicyModel;
import com.medhir.rest.repository.settings.DepartmentRepository;
import com.medhir.rest.repository.settings.LeavePolicyRepository;
import com.medhir.rest.service.company.CompanyService;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
public class DepartmentServiceTest {
    @Mock
    DepartmentRepository departmentRepository;
    @Mock
    LeavePolicyService leavePolicyService;
    @Mock
    SnowflakeIdGenerator snowflakeIdGenerator;
    @Mock
    CompanyService companyService;
    @Mock
    CompanyModel companyModel;
    DepartmentModel departmentModel;
    @Mock
    LeavePolicyModel leavePolicyModel;
    LeavePolicyRepository leavePolicyRepository;
    @InjectMocks
    DepartmentService departmentService;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        departmentModel =new DepartmentModel();
        departmentModel.setName("sales");
        departmentModel.setCompanyId("COMP123");
        departmentModel.setLeavePolicy("LID101");

        companyModel=new CompanyModel();
        companyModel.setCompanyId("COMP123");

//        leavePolicyModel=new LeavePolicyModel() ;
    }
    @Test
    public void DepartmentCreatedSuccesfully() {
        when(companyService.getCompanyById(departmentModel.getCompanyId())).thenReturn(Optional.of(companyModel));
        when(departmentRepository.existsByNameAndCompanyId(departmentModel.getName(), departmentModel.getCompanyId())).thenReturn(false);
        when(leavePolicyService.getLeavePolicyById(departmentModel.getLeavePolicy())).thenReturn(leavePolicyModel);
        when(snowflakeIdGenerator.nextId()).thenReturn(111L);
        when(departmentRepository.save(any(DepartmentModel.class))).thenAnswer(invocation ->
        { DepartmentModel savedDept= invocation.getArgument(0);
            return savedDept;
        });

        DepartmentModel createdDepartment= departmentService.createDepartment(departmentModel);

        assertAll(
                ()->  assertEquals("DEPT111",createdDepartment.getDepartmentId() )
        );
    }

    @Test
    public void ThrowsResourceNotFound_whenCompanyExistAlready(){

        when(companyService.getCompanyById(companyModel.getCompanyId())).thenReturn(Optional.empty());

       ResourceNotFoundException ex= assertThrows(ResourceNotFoundException.class,()->
        {   departmentService.createDepartment(departmentModel);

        });

        assertEquals("Company not found with id: " + "COMP123",ex.getMessage());

    }

    @Test
    public void Departmentnameandcompanyid_alreadyexist(){

        when(companyService.getCompanyById(companyModel.getCompanyId())).thenReturn(Optional.of(companyModel));

        when(departmentRepository.existsByNameAndCompanyId(departmentModel.getName(), departmentModel.getCompanyId())).thenReturn(true);

        DuplicateResourceException ex=  assertThrows(  DuplicateResourceException.class,()->{

            departmentService.createDepartment(departmentModel);
        });

        assertEquals("Department with name " + departmentModel.getName() + " already exists in this company",ex.getMessage());
    }


    @Test
    public void Success_getAllDepartment() {

        // Create a list with the departmentModel you defined in setup()
        List<DepartmentModel> departmentList = Collections.singletonList(departmentModel);

        // Mock repository response
        when(departmentRepository.findAll()).thenReturn(departmentList);

        // Call service method
        List<DepartmentModel> result = departmentService.getAllDepartments();

        // Assertions
        assertAll(
                ()->assertNotNull(result),
                ()->assertEquals(1, result.size()),
                ()->assertEquals("sales", result.get(0).getName()),
                ()->assertEquals("COMP123", result.get(0).getCompanyId()
                ));

    }


    @Test
    public void SuccesswhenGetDepartmentbyId(){

        when(departmentRepository.findByDepartmentId(departmentModel.getDepartmentId())).thenReturn(Optional.of(departmentModel));

        DepartmentModel savedDept=departmentService.getDepartmentById(departmentModel.getDepartmentId());


        assertNotNull(savedDept);
        assertEquals(departmentModel.getName(), savedDept.getName());
        assertEquals(departmentModel.getCompanyId(), savedDept.getCompanyId());
    }



    @Test
    public void testUpdateDepartment_Success() {
        when(departmentRepository.findByDepartmentId("DEPT101")).thenReturn(Optional.of(departmentModel));
        when(departmentRepository.existsByNameAndCompanyId("sales", "COMP123")).thenReturn(false);
        when(departmentRepository.save(any(DepartmentModel.class))).thenAnswer(inv -> inv.getArgument(0));

        DepartmentModel result = departmentService.updateDepartment("DEPT101", departmentModel);

        assertNotNull(result);
        assertEquals("sales", result.getName());
    }



    // Duplicate Department Name Exists Case
    @Test
    public void testUpdateDepartment_DuplicateNameExists() {

        DepartmentModel existingDepartment = new DepartmentModel();
        existingDepartment.setDepartmentId("DEPT101");
        existingDepartment.setName("sales");
        existingDepartment.setCompanyId("COMP123");

        departmentModel = new DepartmentModel();
        departmentModel.setDepartmentId("DEPT101");
        departmentModel.setName("Marketing"); // New name to test duplicate condition
        departmentModel.setCompanyId("COMP123");

        when(departmentRepository.findByDepartmentId("DEPT101")).thenReturn(Optional.of(existingDepartment));
        when(departmentRepository.existsByNameAndCompanyId("Marketing", "COMP123")).thenReturn(true);

        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class, () -> {
            departmentService.updateDepartment("DEPT101", departmentModel);
        });

        assertEquals("Department with name Marketing already exists in this company",ex.getMessage());
    }
}
