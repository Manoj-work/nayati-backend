package com.medhir.rest.service.rbac;

import com.medhir.rest.config.rbac.MasterModulesLoader;
import com.medhir.rest.dto.rbac.roles.CreateRoleRequest;
import com.medhir.rest.dto.rbac.roles.UpdateRoleRequest;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.mapper.rbac.roles.RolesMapper;
import com.medhir.rest.model.EmployeeModel;
import com.medhir.rest.model.rbac.ModulePermission;
import com.medhir.rest.model.rbac.Roles;
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

    public Roles createRole(CreateRoleRequest request) {
        // Validate permissions with master modules (optional but recommended)
        validatePermissions(request.getPermissions());
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

    // Helper method to create the head of company role
    public Roles createHeadOfCompanyRole(String companyId, List<ModulePermission> permissions) {
        String roleName = "HEAD_OF_COMPANY_" + companyId;
        Roles role = new Roles();
        role.setName(roleName);
        role.setCompanyId(companyId);
        role.setPermissions(permissions);
        return rolesRepository.save(role);
    }

    // Helper method to update the head of company role's permissions
    public void updateHeadOfCompanyRolePermissions(String companyId, List<ModulePermission> permissions) {
        String roleName = "HEAD_OF_COMPANY_" + companyId;
        Roles headRole = rolesRepository.findAll().stream()
            .filter(r -> roleName.equals(r.getName()) && companyId.equals(r.getCompanyId()))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Head of company role not found for company: " + companyId));
        headRole.setPermissions(permissions);
        rolesRepository.save(headRole);
    }

    private void validatePermissions(List<ModulePermission> permissions) {
        for (ModulePermission module : permissions) {
            var masterModule = masterModulesLoader.getConfig().getModules().stream()
                    .filter(m -> m.getModuleName().equals(module.getModuleName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid module: " + module.getModuleName()));

            for (var feature : module.getFeatures()) {
                var masterFeature = masterModule.getFeatures().stream()
                        .filter(f -> f.getFeatureName().equals(feature.getFeatureName()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid feature: " + feature.getFeatureName()));

                for (var sub : feature.getSubFeatures()) {
                    var masterSub = masterFeature.getSubFeatures().stream()
                            .filter(sf -> sf.getSubFeatureName().equals(sub.getSubFeatureName()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Invalid sub-feature: " + sub.getSubFeatureName()));

                    for (String action : sub.getActions()) {
                        if (!masterSub.getActions().contains(action)) {
                            throw new IllegalArgumentException("Invalid action: " + action + " for sub-feature: " + sub.getSubFeatureName());
                        }
                    }
                }
            }
        }
    }


}
