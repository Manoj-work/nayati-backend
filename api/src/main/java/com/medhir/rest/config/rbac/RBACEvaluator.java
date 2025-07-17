package com.medhir.rest.config.rbac;

import com.medhir.rest.model.company.CompanyModel;
import com.medhir.rest.model.employee.EmployeeModel;
import com.medhir.rest.model.rbac.Roles;
import com.medhir.rest.repository.CompanyRepository;
import com.medhir.rest.repository.EmployeeRepository;
import com.medhir.rest.repository.rbac.RolesRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("rbacEvaluator")
@RequiredArgsConstructor
public class RBACEvaluator {

    private final EmployeeRepository employeeRepository;
    private final RolesRepository roleRepository;
    private final CompanyRepository companyRepository;

    public boolean hasPermission(
            Authentication authentication,
            String moduleId,
            String featureId,
            String subFeatureId,
            String action
    ) {
        Claims claims = extractAllClaims(authentication);
        List<String> claimRoles = claims.get("roles", List.class);
        if (claimRoles != null && claimRoles.contains("SUPERADMIN")) {
            return true; // full access
        }

        String employeeId = authentication.getName(); // JWT principal → employeeId

        EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        CompanyModel company = companyRepository.findByCompanyId(employee.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found: " + employee.getCompanyId()));

        boolean companyAllows = company.getAssignedModules().stream().anyMatch(mp ->
                mp.getModuleId().equals(moduleId) &&
                        mp.getFeatures().stream().anyMatch(fp ->
                                fp.getFeatureId().equals(featureId) &&
                                        fp.getSubFeatures().stream().anyMatch(sf ->
                                                sf.getSubFeatureId().equals(subFeatureId) &&
                                                        sf.getActions().contains(action)
                                        )
                        )
        );

        if (!companyAllows) {
            return false;
        }

        List<Roles> roles = roleRepository.findByRoleIdIn(employee.getRoleIds());

        return roles.stream().anyMatch(role ->
                role.getPermissions().stream().anyMatch(mp ->
                        mp.getModuleId().equals(moduleId) &&
                                mp.getFeatures().stream().anyMatch(fp ->
                                        fp.getFeatureId().equals(featureId) &&
                                                fp.getSubFeatures().stream().anyMatch(sf ->
                                                        sf.getSubFeatureId().equals(subFeatureId) &&
                                                                sf.getActions().contains(action)
                                                )
                                )
                )
        );
    }
    private Claims extractAllClaims(Authentication authentication) {
        throw new UnsupportedOperationException("extractAllClaims not implemented");
    }
}