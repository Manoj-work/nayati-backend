package com.medhir.rest.service;

import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.CompanyModel;
import com.medhir.rest.repository.CompanyRepository;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @InjectMocks
    private CompanyService companyService;

    private CompanyModel mockCompany;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockCompany = new CompanyModel();
        mockCompany.setCompanyId("CID100");
        mockCompany.setName("Test Company");
        mockCompany.setEmail("test@example.com");
        mockCompany.setPhone("1234567890");
        mockCompany.setGst("GSTIN12345");
        mockCompany.setRegAdd("Test Address");
        mockCompany.setPrefixForEmpID("EMP");
    }
//
    @Test
    public void successWhenCreateCompany() {
        when(companyRepository.findByEmail(mockCompany.getEmail())).thenReturn(Optional.empty());
        when(companyRepository.findByPhone(mockCompany.getPhone())).thenReturn(Optional.empty());
        when(snowflakeIdGenerator.nextId()).thenReturn(100L);
//        when(companyRepository.save(any(CompanyModel.class))).thenReturn(mockCompany);
         when(companyRepository.save(mockCompany)).thenReturn(mockCompany);
        CompanyModel savedCompany = companyService.createCompany(mockCompany);

        assertAll(
                "saved company is not null",
                () -> assertNotNull(savedCompany,"company should not be null"),
                () -> assertEquals("CID100", savedCompany.getCompanyId()));

//        verify(companyRepository).save(any(CompanyModel.class));


    }


    @Test
    public void ThrowException_WhenEmailAlreadyExists_duringCompanyCreation() {

        // Mocking repository to simulate that email is already present
        when(companyRepository.findByEmail(mockCompany.getEmail()))
                .thenReturn(Optional.of(mockCompany));

        // Asserting that DuplicateResourceException is thrown during creation
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> companyService.createCompany(mockCompany));

        // Verifying the exception message is as expected
        assertEquals("Email already exists: " + mockCompany.getEmail(), exception.getMessage());

        // Optional: Ensure save() is never called when email already exists
        verify(companyRepository, never()).save(any(CompanyModel.class));
    }



    @Test
    public void ThrowException_whenPhoneNoAlreadyExists_duringCompanyCreation(){

        //mock repository to simulate phone no is already present
        when(companyRepository.findByEmail(mockCompany.getEmail())).thenReturn(Optional.empty());

        when(companyRepository.findByPhone(mockCompany.getPhone())).thenReturn(Optional.of(mockCompany));

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, ()-> companyService.createCompany(mockCompany));

        assertEquals("Phone number already exists: " + mockCompany.getPhone(), exception.getMessage());

        verify(companyRepository,never()).save(any(CompanyModel.class));
    }

    @Test
    public void get_listof_allcompanies() {
        List<CompanyModel> companies = Arrays.asList(mockCompany);
        when(companyRepository.findAll()).thenReturn(companies);

        List<CompanyModel> result = companyService.getAllCompanies();
       assertAll(
               "as given only 1 company",
               ()->   assertEquals(1, result.size()),
               ()-> assertEquals(mockCompany.getName(), result.get(0).getName())
         );

        verify(companyRepository).findAll();
    }
//
    @Test
    public void Successwhenupdatedcompany() {
        CompanyModel result ;

        CompanyModel updatedCompany = new CompanyModel();
        updatedCompany.setName("Updated Company");
        updatedCompany.setEmail("updated@example.com");
        updatedCompany.setPhone("9876543210");
        updatedCompany.setGst("GSTUPDATED");
        updatedCompany.setRegAdd("New Address");
        updatedCompany.setPrefixForEmpID("NEW");

        when(companyRepository.findByCompanyId(mockCompany.getCompanyId())).thenReturn(Optional.of(mockCompany));
        when(companyRepository.findByEmail(updatedCompany.getEmail())).thenReturn(Optional.empty());
        when(companyRepository.findByPhone(updatedCompany.getPhone())).thenReturn(Optional.empty());
        when(companyRepository.save(any(CompanyModel.class))).thenReturn(mockCompany);

          result = companyService.updateCompany(mockCompany.getCompanyId(), updatedCompany);
    assertAll(
            ()->   assertNotNull(result),
            ()->  assertEquals("Updated Company", result.getName()),
            ()->  assertEquals("updated@example.com", result.getEmail() ) );

             verify(companyRepository).save(any(CompanyModel.class));


    }

    @ParameterizedTest
    @CsvSource({
            "Company1, updated1@example.com, 9111111111",
            "Company2, updated2@example.com, 9222222222"
    })
    public void SuccesswhenUpdateCompany(String name, String email, String phone) {
        CompanyModel updated = new CompanyModel();
        updated.setName(name);
        updated.setEmail(email);
        updated.setPhone(phone);

        when(companyRepository.findByCompanyId(mockCompany.getCompanyId())).thenReturn(Optional.of(mockCompany));
        when(companyRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(companyRepository.findByPhone(phone)).thenReturn(Optional.empty());
        when(companyRepository.save(any(CompanyModel.class))).thenReturn(mockCompany);

        CompanyModel result = companyService.updateCompany(mockCompany.getCompanyId(), updated);
        assertNotNull(result);
    }

    @Test
    public void NotFoundErrorwhendoUpdateCompany_() {
        CompanyModel updatedCompany = new CompanyModel();
        updatedCompany.setName("Updated Company");
        updatedCompany.setEmail("updated@example.com");
        updatedCompany.setPhone("9876543210");
        when(companyRepository.findByCompanyId("CID999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> companyService.updateCompany("CID999", updatedCompany));
        assertEquals("Company not found with ID: CID999", exception.getMessage());
    }

    @Test
    public void DuplicateEmailErrorWhenUpdateCompany_() {
        CompanyModel existingCompany = new CompanyModel();
        existingCompany.setCompanyId("CID200");
        existingCompany.setEmail("existing@example.com");

        CompanyModel updatedCompany = new CompanyModel();
        updatedCompany.setName("Updated Company");
        updatedCompany.setEmail("existing@example.com"); // Same email as existing company
        updatedCompany.setPhone("9876543210");

        when(companyRepository.findByCompanyId(mockCompany.getCompanyId())).thenReturn(Optional.of(mockCompany));
        when(companyRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingCompany));

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
            () -> companyService.updateCompany(mockCompany.getCompanyId(), updatedCompany));
        assertEquals("Email already exists: existing@example.com", exception.getMessage());
    }
//
    @Test
    public void DuplicatePhonenoErrorWhenUpdateCompany_() {
        CompanyModel existingCompany = new CompanyModel();
        existingCompany.setCompanyId("CID200");
        existingCompany.setPhone("9876543210");
        CompanyModel updatedCompany = new CompanyModel();
        updatedCompany.setName("Updated Company");
        updatedCompany.setEmail("updated@example.com");
        updatedCompany.setPhone("9876543210"); // Same phone as existing company

        when(companyRepository.findByCompanyId(mockCompany.getCompanyId())).thenReturn(Optional.of(mockCompany));
        when(companyRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(companyRepository.findByPhone("9876543210")).thenReturn(Optional.of(existingCompany));

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
            () -> companyService.updateCompany(mockCompany.getCompanyId(), updatedCompany));
        assertEquals("Phone number already exists: 9876543210", exception.getMessage());
    }
//
    @Test
    public void SuccessWhenDeleteCompany_() {
        when(companyRepository.existsByCompanyId(mockCompany.getCompanyId())).thenReturn(true);

        companyService.deleteCompany(mockCompany.getCompanyId());

        verify(companyRepository, times(1)).deleteByCompanyId(mockCompany.getCompanyId());
    }

    @Test
    public void NotFoundErrorwhenDeleteCompany_() {
        when(companyRepository.existsByCompanyId("CID999")).thenReturn(false);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
            () -> companyService.deleteCompany("CID999"));
        assertEquals("Company not found with ID: CID999", exception.getMessage());
    }

    @Test
    public void Success_whento_findGetCompanyById_() {
        when(companyRepository.findByCompanyId(mockCompany.getCompanyId())).thenReturn(Optional.of(mockCompany));

        Optional<CompanyModel> result = companyService.getCompanyById(mockCompany.getCompanyId());

        assertTrue(result.isPresent());
        assertAll(
                       "as getting company by id",
                       ()-> assertEquals(mockCompany.getName(), result.get().getName()),
                       ()-> assertEquals(mockCompany.getEmail(), result.get().getEmail())
        );

    }

    @Test
    public void NotFoundErrrorwhenGetCompanyById_() {
        when(companyRepository.findByCompanyId("CID999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> companyService.getCompanyById("CID999"));
        assertEquals("Company not found with ID: CID999", exception.getMessage());
    }

//    @Test
//    public void testGetAllCompanies_Multiple() {
//        CompanyModel company1 = new CompanyModel();
//        company1.setCompanyId("CID101");
//        company1.setName("Company One");
//
//        CompanyModel company2 = new CompanyModel();
//        company2.setCompanyId("CID102");
//        company2.setName("Company Two");
//
//        List<CompanyModel> companies = Arrays.asList(company1, company2);
//
//        when(companyRepository.findAll()).thenReturn(companies);
//
//        List<CompanyModel> result = companyService.getAllCompanies();
//
//        assertEquals(2, result.size());
//        assertEquals("Company One", result.get(0).getName());
//        assertEquals("Company Two", result.get(1).getName());
//        verify(companyRepository).findAll();
//    }
}

