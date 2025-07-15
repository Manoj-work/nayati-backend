package com.medhir.rest.controller.rbac.roles;

import com.medhir.rest.dto.rbac.roles.CreateRoleRequest;
import com.medhir.rest.dto.rbac.roles.UpdateRoleRequest;
import com.medhir.rest.model.rbac.Roles;
import com.medhir.rest.service.rbac.RolesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/superadmin/roles")
@RequiredArgsConstructor
public class RolesController {

    private final RolesService rolesService;

    @PostMapping
    public ResponseEntity<Roles> createRole(@Valid @RequestBody CreateRoleRequest request) {
        Roles role = rolesService.createRole(request);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<Roles> updateRole(@PathVariable String roleId, @RequestBody UpdateRoleRequest request) {
        Roles updated = rolesService.updateRole(roleId, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/assign/{employeeId}")
    public ResponseEntity<String> assignRoles(
            @PathVariable String employeeId,
            @RequestBody List<String> roleIds
    ) {
        rolesService.assignRolesToEmployee(employeeId, roleIds);
        return ResponseEntity.ok("Roles assigned successfully.");
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Roles> getRoleById(@PathVariable String roleId) {
        Roles role = rolesService.getRoleById(roleId);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    public ResponseEntity<List<Roles>> getAllRoles() {
        List<Roles> roles = rolesService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Roles>> getRolesByCompanyId(@PathVariable String companyId) {
        List<Roles> roles = rolesService.getRolesByCompanyId(companyId);
        return ResponseEntity.ok(roles);
    }



}