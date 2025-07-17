package com.medhir.rest.service.company;

import com.medhir.rest.config.rbac.MasterModulesConfig;
import com.medhir.rest.config.rbac.MasterModulesLoader;
import com.medhir.rest.dto.company.CompanyHeadDetailsDTO;
import com.medhir.rest.dto.company.CreateCompanyWithHeadRequest;
import com.medhir.rest.dto.rbac.SimpleModule;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.company.CompanyModel;
import com.medhir.rest.model.employee.EmployeeModel;
import com.medhir.rest.model.rbac.FeaturePermission;
import com.medhir.rest.model.rbac.ModulePermission;
import com.medhir.rest.model.rbac.SubFeaturePermission;
import com.medhir.rest.model.settings.DepartmentModel;
import com.medhir.rest.model.settings.DesignationModel;
import com.medhir.rest.repository.company.CompanyRepository;
import com.medhir.rest.service.employee.EmployeeService;
import com.medhir.rest.service.settings.DepartmentService;
import com.medhir.rest.service.settings.DesignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyOrchestratorService {

    private final CompanyService companyService;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final DesignationService designationService;
    private final MasterModulesLoader masterModulesLoader;
    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyModel createCompanyWithHead(CreateCompanyWithHeadRequest request) {
        var companyDto = request.getCompany();
        var headsDto = request.getCompanyHeads();
        List<ModulePermission> modulePermissions = mapMasterModulesToPermissions(
                masterModulesLoader.getConfig().getModules()
        );

        // 1.Create company
        CompanyModel company = new CompanyModel();
        company.setName(companyDto.getName());
        company.setEmail(companyDto.getEmail());
        company.setPhone(companyDto.getPhone());
        company.setGst(companyDto.getGst());
        company.setRegAdd(companyDto.getRegAdd());
        company.setPrefixForEmpID(companyDto.getPrefixForEmpID());
        company.setAssignedModules(modulePermissions); // assign all modules to the company
        company = companyService.createCompany(company);

        // 2. Create "CompanyHead - <CompanyName>" department
        DepartmentModel department = new DepartmentModel();
        department.setCompanyId(company.getCompanyId());
        department.setName("CompanyHead - " + company.getName());
        department.setDescription("Department for company heads");
        List<SimpleModule> allModules = masterModulesLoader.getConfig().getModules().stream()
                .map(m -> new SimpleModule(m.getModuleId(), m.getModuleName()))
                .toList();
        department.setAssignedModules(allModules); // default modules assigned
        department = departmentService.createDepartment(department);

        // 3. Create "Company Head" designation
        DesignationModel designation = new DesignationModel();
        designation.setName("Company Head");
        designation.setDepartment(department.getDepartmentId());
        designation.setDescription("Designation for company heads");
        designation.setCompanyHead(true);
        designation.setAdmin(true); // optional
        designation.setManager(true); // optional
        designation = designationService.createDesignation(designation);

        // 4. Create all head employees
        Set<String> headEmployeeIds = new HashSet<>();
        for (CompanyHeadDetailsDTO headDto : headsDto) {
            EmployeeModel headEmployee = employeeService.createCompanyHeadEmployee(
                    company.getCompanyId(),
                    department.getDepartmentId(),
                    designation.getDesignationId(),
                    headDto.getFirstName(),
                    headDto.getMiddleName(),
                    headDto.getLastName(),
                    headDto.getEmail(),
                    headDto.getPhone()
            );
            headEmployeeIds.add(headEmployee.getEmployeeId());
        }

        // 5. Set company head list
        company.setCompanyHeadIds((headEmployeeIds));
        return updateCompany(company.getCompanyId(), company);
    }
    public CompanyModel updateCompany(String companyId, CompanyModel company) {
        Optional<CompanyModel> existingCompanyOpt = companyRepository.findByCompanyId(companyId);
        if (existingCompanyOpt.isEmpty()) {
            throw new ResourceNotFoundException("Company not found with ID: " + companyId);
        }

        CompanyModel existingCompany = existingCompanyOpt.get();

        // Prevent changing companyId
        if (!companyId.equals(existingCompany.getCompanyId())) {
            throw new IllegalArgumentException("Company ID cannot be changed.");
        }

        // Check for unique email
        if (!existingCompany.getEmail().equals(company.getEmail()) &&
                companyRepository.findByEmail(company.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists: " + company.getEmail());
        }

        // Check for unique phone number
        if (!existingCompany.getPhone().equals(company.getPhone()) &&
                companyRepository.findByPhone(company.getPhone()).isPresent()) {
            throw new DuplicateResourceException("Phone number already exists: " + company.getPhone());
        }

        // Handle company head changes
        Set<String> oldHeadIds = existingCompany.getCompanyHeadIds() != null ? existingCompany.getCompanyHeadIds() : Set.of();
        Set<String> newHeadIds = company.getCompanyHeadIds() != null ? company.getCompanyHeadIds() : Set.of();

        Set<String> headsToRemove = oldHeadIds.stream()
                .filter(id -> !newHeadIds.contains(id))
                .collect(Collectors.toSet());

        Set<String> headsToAdd = newHeadIds.stream()
                .filter(id -> !oldHeadIds.contains(id))
                .collect(Collectors.toSet());

        // Remove COMPANY_HEAD role from removed heads
        for (String empId : headsToRemove) {
            employeeService.removeRoleFromEmployee(empId, "COMPANY_HEAD");
        }

        // Add COMPANY_HEAD role to new heads
        for (String empId : headsToAdd) {
            employeeService.addRoleToEmployee(empId, "COMPANY_HEAD");
        }

        // Update company fields
        existingCompany.setName(company.getName());
        existingCompany.setEmail(company.getEmail());
        existingCompany.setPhone(company.getPhone());
        existingCompany.setGst(company.getGst());
        existingCompany.setRegAdd(company.getRegAdd());
        existingCompany.setPrefixForEmpID(company.getPrefixForEmpID());
        existingCompany.setColorCode(company.getColorCode());
        existingCompany.setCompanyHeadIds(newHeadIds);

        // Replace assigned modules only if provided (not null)
        if (company.getAssignedModules() != null && !company.getAssignedModules().isEmpty()) {
            existingCompany.setAssignedModules(company.getAssignedModules());
        }

        return companyRepository.save(existingCompany);
    }



        public static List<ModulePermission> mapMasterModulesToPermissions(List<MasterModulesConfig.Module> masterModules) {
            return masterModules.stream().map(module -> {
                List<FeaturePermission> featurePermissions = module.getFeatures().stream().map(feature -> {
                    List<SubFeaturePermission> subFeaturePermissions = feature.getSubFeatures().stream().map(sub -> {
                        return new SubFeaturePermission(
                                sub.getSubFeatureId(),
                                sub.getSubFeatureName(),
                                sub.getActions()
                        );
                    }).toList();

                    return new FeaturePermission(
                            feature.getFeatureId(),
                            feature.getFeatureName(),
                            subFeaturePermissions
                    );
                }).toList();

                return new ModulePermission(
                        module.getModuleId(),
                        module.getModuleName(),
                        featurePermissions
                );
            }).toList();
        }
}
