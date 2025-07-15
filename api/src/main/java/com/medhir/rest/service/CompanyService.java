package com.medhir.rest.service;

import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.CompanyModel;
import com.medhir.rest.repository.CompanyRepository;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import com.medhir.rest.dto.EmployeeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    @Lazy
    private EmployeeService employeeService;

    public  CompanyModel createCompany(CompanyModel company) {
        // Check if email already exists
        if (companyRepository.findByEmail(company.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists: " + company.getEmail());
        }
        // Check if phone number already exists
         if (companyRepository.findByPhone(company.getPhone()).isPresent()) {
            throw new DuplicateResourceException("Phone number already exists: " + company.getPhone());
        }

        company.setCompanyId("CID" + snowflakeIdGenerator.nextId());

        return companyRepository.save(company);
    }

    @Transactional
    public CompanyModel createCompanyWithHead(CompanyModel company) {
        // 1. Create company first
        CompanyModel savedCompany = createCompany(company);
        
        // 2. If company head exists, create employee
        if (savedCompany.getCompanyHead() != null && savedCompany.getCompanyHead().getFirstName() != null) {
            EmployeeDTO employeeDTO = new EmployeeDTO();
            employeeDTO.setCompanyId(savedCompany.getCompanyId());
            employeeDTO.setFirstName(savedCompany.getCompanyHead().getFirstName());
            employeeDTO.setMiddleName(savedCompany.getCompanyHead().getMiddleName());
            employeeDTO.setLastName(savedCompany.getCompanyHead().getLastName());
            employeeDTO.setEmailPersonal(savedCompany.getCompanyHead().getEmail());
            employeeDTO.setPhone(savedCompany.getCompanyHead().getPhone());
            employeeDTO.setRoles(Set.of("EMPLOYEE")); // Default role
            employeeDTO.setJoiningDate(LocalDate.now());
            
            // Use existing employee service (no images needed for company head)
            try {
                employeeService.createEmployee(employeeDTO, null, null, null, null, null, null, null);
            } catch (Exception e) {
                // Log error but don't fail company creation
                System.err.println("Failed to create company head as employee: " + e.getMessage());
            }
        }
        
        return savedCompany;
    }

    public List<CompanyModel> getAllCompanies() {
        return companyRepository.findAll();
    }

    public CompanyModel updateCompany(String companyId, CompanyModel company) {
        Optional<CompanyModel> existingCompany = companyRepository.findByCompanyId(companyId);
        if (existingCompany.isEmpty()) {
            throw new ResourceNotFoundException("Company not found with ID: " + companyId);
        }

        CompanyModel companyToUpdate = existingCompany.get();

        // Check for unique email
        if (!companyToUpdate.getEmail().equals(company.getEmail()) &&
                companyRepository.findByEmail(company.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists: " + company.getEmail());
        }

        // Check for unique phone number
        if (!companyToUpdate.getPhone().equals(company.getPhone()) &&
                companyRepository.findByPhone(company.getPhone()).isPresent()) {
            throw new DuplicateResourceException("Phone number already exists: " + company.getPhone());
        }

        companyToUpdate.setName(company.getName());
        companyToUpdate.setEmail(company.getEmail());
        companyToUpdate.setPhone(company.getPhone());
        companyToUpdate.setGst(company.getGst());
        companyToUpdate.setRegAdd(company.getRegAdd());
        companyToUpdate.setPrefixForEmpID(company.getPrefixForEmpID());
        companyToUpdate.setColorCode(company.getColorCode());
        companyToUpdate.setCompanyHead(company.getCompanyHead());

        return companyRepository.save(companyToUpdate);
    }

    public void deleteCompany(String companyId) {
        if (!companyRepository.existsByCompanyId(companyId)) {
            throw new DuplicateResourceException("Company not found with ID: " + companyId);
        }
        companyRepository.deleteByCompanyId(companyId);
    }
    public Optional<CompanyModel> getCompanyById(String companyId) {
        return Optional.ofNullable(companyRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId)));
    }
}