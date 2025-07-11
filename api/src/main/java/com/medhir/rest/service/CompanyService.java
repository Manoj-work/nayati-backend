package com.medhir.rest.service;

import com.medhir.rest.config.rbac.MasterModulesLoader;
import com.medhir.rest.dto.rbac.AssignModulesRequest;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.mapper.rbac.AssignModulesMapper;
import com.medhir.rest.model.CompanyModel;
import com.medhir.rest.model.rbac.FeaturePermission;
import com.medhir.rest.model.rbac.ModulePermission;
import com.medhir.rest.model.rbac.SubFeaturePermission;
import com.medhir.rest.repository.CompanyRepository;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import com.medhir.rest.model.Roles;
import com.medhir.rest.model.EmployeeModel;
import com.medhir.rest.repository.EmployeeRepository;
import com.medhir.rest.service.RolesService;


@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private MasterModulesLoader masterModulesLoader;
    @Autowired
    private AssignModulesMapper assignModulesMapper;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RolesService rolesService;


    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

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

        CompanyModel savedCompany = companyRepository.save(company);

        // Create head of company role and assign to head
        if (company.getHeadEmployeeId() != null && !company.getHeadEmployeeId().isEmpty()) {
            // Create the role with current assigned modules (may be empty at creation)
            Roles headRole = rolesService.createHeadOfCompanyRole(savedCompany.getCompanyId(), savedCompany.getAssignedModules() != null ? savedCompany.getAssignedModules() : List.of());
            // Assign to head employee
            EmployeeModel head = employeeRepository.findByEmployeeId(company.getHeadEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Head employee not found: " + company.getHeadEmployeeId()));
            if (head.getRoleIds() == null) head.setRoleIds(new java.util.ArrayList<>());
            head.getRoleIds().add(headRole.getRoleId());
            employeeRepository.save(head);
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


    // services for RBAC

    public void assignModulesToCompany(String companyId, AssignModulesRequest request) {

        CompanyModel company = companyRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        // Validate against master modules config
        for (AssignModulesRequest.ModuleRequest moduleDTO : request.getModules()) {
            var masterModule = masterModulesLoader.getConfig().getModules().stream()
                    .filter(m -> m.getId().equals(moduleDTO.getModuleId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid module ID: " + moduleDTO.getModuleId()));

            for (AssignModulesRequest.FeatureRequest featureDTO : moduleDTO.getFeatures()) {
                var masterFeature = masterModule.getFeatures().stream()
                        .filter(f -> f.getId().equals(featureDTO.getFeatureId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid feature ID: " + featureDTO.getFeatureId()));

                for (AssignModulesRequest.SubFeatureRequest subFeatureDTO : featureDTO.getSubFeatures()) {
                    var masterSubFeature = masterFeature.getSubFeatures().stream()
                            .filter(sf -> sf.getId().equals(subFeatureDTO.getSubFeatureId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Invalid sub-feature ID: " + subFeatureDTO.getSubFeatureId()));

                    for (String action : subFeatureDTO.getActions()) {
                        if (!masterSubFeature.getActions().contains(action)) {
                            throw new IllegalArgumentException("Invalid action '" + action + "' for sub-feature ID: " + subFeatureDTO.getSubFeatureId());
                        }
                    }
                }
            }
        }

        // Map DTOs to entity using MapStruct
        List<ModulePermission> permissions = assignModulesMapper.toModulePermissions(request.getModules());
        company.setAssignedModules(permissions);
        companyRepository.save(company);

        // Update head of company role permissions
        rolesService.updateHeadOfCompanyRolePermissions(companyId, permissions);
    }

    public List<ModulePermission> getAssignedModules(String companyId) {
        CompanyModel company = companyRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));
        return company.getAssignedModules();
    }

    public void removeModuleFromCompany(String companyId, String moduleId) {
        CompanyModel company = companyRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        boolean removed = company.getAssignedModules().removeIf(m ->
                m.getModuleId().equals(moduleId)
        );

        if (!removed) {
            throw new ResourceNotFoundException("Module with ID: " + moduleId + " not found for this company");
        }

        companyRepository.save(company);
    }


}