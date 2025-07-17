package com.medhir.rest.service.company;

import com.medhir.rest.config.rbac.MasterModulesLoader;
import com.medhir.rest.dto.company.CompanyResponseDTO;
import com.medhir.rest.dto.rbac.AssignModulesRequest;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.mapper.company.CompanyMapper;
import com.medhir.rest.mapper.rbac.AssignModulesMapper;
import com.medhir.rest.model.company.CompanyModel;
import com.medhir.rest.model.employee.EmployeeModel;
import com.medhir.rest.model.rbac.ModulePermission;
import com.medhir.rest.repository.company.CompanyRepository;
import com.medhir.rest.repository.employee.EmployeeRepository;
import com.medhir.rest.service.employee.EmployeeService;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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

            if (company.getCompanyHeadIds() != null && !company.getCompanyHeadIds().isEmpty()) {
                List<EmployeeModel> heads = employeeRepository.findByEmployeeIdIn(company.getCompanyHeadIds());
                dto.setCompanyHeads(heads.stream()
                        .map(companyMapper::toCompanyHeadResponseDTO)
                        .collect(Collectors.toList()));
            }

            return dto;
        }).collect(Collectors.toList());
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

        if (company.getCompanyHeadIds() != null && !company.getCompanyHeadIds().isEmpty()) {
            List<EmployeeModel> heads = employeeRepository.findByEmployeeIdIn(company.getCompanyHeadIds());

            if (heads.size() != company.getCompanyHeadIds().size()) {
                throw new ResourceNotFoundException("One or more company heads not found for IDs: " + company.getCompanyHeadIds());
            }

            dto.setCompanyHeads(heads.stream()
                    .map(companyMapper::toCompanyHeadResponseDTO)
                    .collect(Collectors.toList()));
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