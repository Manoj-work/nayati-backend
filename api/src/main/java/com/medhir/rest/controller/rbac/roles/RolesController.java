package com.medhir.rest.controller.rbac.roles;

import com.medhir.rest.dto.rbac.roles.CreateRoleRequest;
import com.medhir.rest.dto.rbac.roles.UpdateRoleRequest;
import com.medhir.rest.model.rbac.Roles;
import com.medhir.rest.service.rbac.RolesService;
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
    public ResponseEntity<Roles> createRole(@RequestBody CreateRoleRequest request) {
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
}