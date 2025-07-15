package com.medhir.rest.service.rbac;

import com.medhir.rest.config.rbac.MasterModulesLoader;
import com.medhir.rest.dto.rbac.roles.CreateRoleRequest;
import com.medhir.rest.dto.rbac.roles.UpdateRoleRequest;
import com.medhir.rest.exception.BadRequestException;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.PermissionDeniedException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.mapper.rbac.roles.RolesMapper;
import com.medhir.rest.model.CompanyModel;
import com.medhir.rest.model.EmployeeModel;
import com.medhir.rest.model.rbac.FeaturePermission;
import com.medhir.rest.model.rbac.ModulePermission;
import com.medhir.rest.model.rbac.Roles;
import com.medhir.rest.model.rbac.SubFeaturePermission;
import com.medhir.rest.repository.CompanyRepository;
import com.medhir.rest.repository.EmployeeRepository;
import com.medhir.rest.repository.rbac.RolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolesService {

    private final RolesRepository rolesRepository;
    private final MasterModulesLoader masterModulesLoader;
    private final RolesMapper rolesMapper;
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;

    public Roles createRole(CreateRoleRequest request) {
        CompanyModel company = companyRepository.findByCompanyId(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        //Duplicate name check within this company
        boolean exists = rolesRepository.existsByRoleNameAndCompanyId(request.getRoleName(),request.getCompanyId());
        if (exists) {
            throw new DuplicateResourceException("Role name already eists for this company : " + request.getRoleName());
        }

        // Validate requested permissions against company's assignedModules
        request.getPermissions().forEach(requestedModule -> {
            ModulePermission companyModule = company.getAssignedModules().stream()
                    .filter(m -> m.getModuleId().equals(requestedModule.getModuleId()))
                    .findFirst()
                    .orElseThrow(() -> new PermissionDeniedException("Module not assigned to company: " + requestedModule.getModuleId()));

            requestedModule.getFeatures().forEach(requestedFeature -> {
                FeaturePermission companyFeature = companyModule.getFeatures().stream()
                        .filter(f -> f.getFeatureId().equals(requestedFeature.getFeatureId()))
                        .findFirst()
                        .orElseThrow(() -> new PermissionDeniedException("Feature not assigned to company: " + requestedFeature.getFeatureId()));

                requestedFeature.getSubFeatures().forEach(requestedSub -> {
                    SubFeaturePermission companySub = companyFeature.getSubFeatures().stream()
                            .filter(sf -> sf.getSubFeatureId().equals(requestedSub.getSubFeatureId()))
                            .findFirst()
                            .orElseThrow(() -> new PermissionDeniedException("SubFeature not assigned to company: " + requestedSub.getSubFeatureId()));

                    requestedSub.getActions().forEach(action -> {
                        if (!companySub.getActions().contains(action)) {
                            throw new PermissionDeniedException("Action " + action + " not allowed for SubFeature: " + requestedSub.getSubFeatureId());
                        }
                    });
                });
            });
        });

        // Map & save
        Roles role = rolesMapper.toRoles(request);
        return rolesRepository.save(role);
    }


    public Roles updateRole(String roleId, UpdateRoleRequest request) {
        Roles role = rolesRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleId));

        // Validate new permissions
        validatePermissions(request.getPermissions());

        rolesMapper.updateRolesFromRequest(request, role);
        return rolesRepository.save(role);
    }

    public void assignRolesToEmployee(String employeeId, List<String> roleIds) {
        EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        employee.setRoleIds(roleIds);
        employeeRepository.save(employee);
    }

    private void validatePermissions(List<ModulePermission> permissions) {
        for (ModulePermission module : permissions) {
            var masterModule = masterModulesLoader.getConfig().getModules().stream()
                    .filter(m -> m.getModuleName().equals(module.getModuleName()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Invalid module: " + module.getModuleName()));

            for (var feature : module.getFeatures()) {
                var masterFeature = masterModule.getFeatures().stream()
                        .filter(f -> f.getFeatureName().equals(feature.getFeatureName()))
                        .findFirst()
                        .orElseThrow(() -> new BadRequestException("Invalid feature: " + feature.getFeatureName()));

                for (var sub : feature.getSubFeatures()) {
                    var masterSub = masterFeature.getSubFeatures().stream()
                            .filter(sf -> sf.getSubFeatureName().equals(sub.getSubFeatureName()))
                            .findFirst()
                            .orElseThrow(() -> new BadRequestException("Invalid sub-feature: " + sub.getSubFeatureName()));

                    for (String action : sub.getActions()) {
                        if (!masterSub.getActions().contains(action)) {
                            throw new BadRequestException("Invalid action: " + action + " for sub-feature: " + sub.getSubFeatureName());
                        }
                    }
                }
            }
        }
    }
    public Roles getRoleById(String roleId) {
        return rolesRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));
    }

    public List<Roles> getAllRoles() {
        return rolesRepository.findAll();
    }

    public List<Roles> getRolesByCompanyId(String companyId) {
        return rolesRepository.findByCompanyId(companyId);
    }


}
