package com.medhir.rest.controller.rbac.modulePermission;

import com.medhir.rest.dto.rbac.AssignModulesRequest;
import com.medhir.rest.model.rbac.ModulePermission;
import com.medhir.rest.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/superadmin/companies")
@RequiredArgsConstructor
public class CompanyModuleController {
    @Autowired
    CompanyService companyService;

    // used to assign and unassign the modules permission to the company
    @PutMapping("/{companyId}/modules")
    public ResponseEntity<Map<String, Object>> assignModulesToCompany(
            @PathVariable String companyId,
            @Valid @RequestBody AssignModulesRequest request
    ) {
        companyService.assignModulesToCompany(companyId, request);
        return ResponseEntity.ok(Map.of(
                "message", "Modules assigned successfully!"
        ));
    }

    @GetMapping("/{companyId}/modules")
    public ResponseEntity<List<ModulePermission>> getAssignedModules(
            @PathVariable String companyId
    ) {
        List<ModulePermission> modules = companyService.getAssignedModules(companyId);
        return ResponseEntity.ok(modules);
    }

}
