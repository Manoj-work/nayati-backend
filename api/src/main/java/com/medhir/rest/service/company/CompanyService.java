package com.medhir.rest.service.company;

import com.medhir.rest.config.rbac.MasterModulesLoader;
import com.medhir.rest.dto.company.CompanyResponseDTO;
import com.medhir.rest.dto.rbac.AssignModulesRequest;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.mapper.company.CompanyMapper;
import com.medhir.rest.mapper.rbac.AssignModulesMapper;
import com.medhir.rest.model.CompanyModel;
import com.medhir.rest.model.EmployeeModel;
import com.medhir.rest.model.rbac.ModulePermission;
import com.medhir.rest.repository.CompanyRepository;
import com.medhir.rest.repository.EmployeeRepository;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private MasterModulesLoader masterModulesLoader;
    @Autowired
    private AssignModulesMapper assignModulesMapper;

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

    public List<CompanyResponseDTO> getAllCompanies() {
        return companyRepository.findAll().stream().map(company -> {
            CompanyResponseDTO dto = companyMapper.toCompanyResponseDTO(company);

            if (company.getCompanyHeadId() != null) {
                employeeRepository.findByEmployeeId(company.getCompanyHeadId()).ifPresent(head -> {
                    dto.setCompanyHead(companyMapper.toCompanyHeadResponseDTO(head));
                });
            }

            return dto;
        }).collect(Collectors.toList());
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
        companyToUpdate.setCompanyHeadId(company.getCompanyHeadId());


        return companyRepository.save(companyToUpdate);
    }

    public void deleteCompany(String companyId) {
        if (!companyRepository.existsByCompanyId(companyId)) {
            throw new DuplicateResourceException("Company not found with ID: " + companyId);
        }
        companyRepository.deleteByCompanyId(companyId);
    }

    public Optional<CompanyModel> getCompanyById(String companyId) {
        return companyRepository.findByCompanyId(companyId);
    }

    public CompanyResponseDTO getCompanywithHeaddetailsById(String companyId) {
        CompanyModel company = companyRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        CompanyResponseDTO dto = companyMapper.toCompanyResponseDTO(company);

        if (company.getCompanyHeadId() != null) {
            EmployeeModel head = employeeRepository.findByEmployeeId(company.getCompanyHeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company Head not found with ID: " + company.getCompanyHeadId()));
            dto.setCompanyHead(companyMapper.toCompanyHeadResponseDTO(head));
        }

        return dto;
    }
    // services for RBAC

    public void assignModulesToCompany(String companyId, AssignModulesRequest request) {

        CompanyModel company = companyRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        // Validate against master modules config
        for (AssignModulesRequest.ModuleRequest moduleDTO : request.getAssignedModules()) {
            var masterModule = masterModulesLoader.getConfig().getModules().stream()
                    .filter(m -> m.getModuleId().equals(moduleDTO.getModuleId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid module ID: " + moduleDTO.getModuleId()));

            for (AssignModulesRequest.FeatureRequest featureDTO : moduleDTO.getFeatures()) {
                var masterFeature = masterModule.getFeatures().stream()
                        .filter(f -> f.getFeatureId().equals(featureDTO.getFeatureId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid feature ID: " + featureDTO.getFeatureId()));

                for (AssignModulesRequest.SubFeatureRequest subFeatureDTO : featureDTO.getSubFeatures()) {
                    var masterSubFeature = masterFeature.getSubFeatures().stream()
                            .filter(sf -> sf.getSubFeatureId().equals(subFeatureDTO.getSubFeatureId()))
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
        List<ModulePermission> permissions = assignModulesMapper.toModulePermissions(request.getAssignedModules());
        company.setAssignedModules(permissions);
        companyRepository.save(company);

        // Update head of company role permissions
    }

    public List<ModulePermission> getAssignedModules(String companyId) {
        CompanyModel company = companyRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));
        return company.getAssignedModules();
    }

}