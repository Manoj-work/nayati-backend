package com.medhir.rest.controller.settings;

import com.medhir.rest.dto.rbac.AssignModulesRequest;
import com.medhir.rest.dto.rbac.SimpleModule;
import com.medhir.rest.model.settings.DepartmentModel;
import com.medhir.rest.service.settings.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createDepartment(@Valid @RequestBody DepartmentModel department) {
        department.setDepartmentId(null); // Clear any existing departmentId
        DepartmentModel createdDepartment = departmentService.createDepartment(department);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Department created successfully");
        response.put("departmentId", createdDepartment.getDepartmentId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentModel>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<DepartmentModel>> getDepartmentsByCompanyId(@PathVariable String companyId) {
        return ResponseEntity.ok(departmentService.getDepartmentsByCompanyId(companyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentModel> getDepartmentById(@PathVariable String id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateDepartment(
            @PathVariable String id,
            @Valid @RequestBody DepartmentModel department) {
        department.setDepartmentId(null); // Clear any existing departmentId
        departmentService.updateDepartment(id, department);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Department updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDepartment(@PathVariable String id) {
        departmentService.deleteDepartment(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Department deleted successfully");
        return ResponseEntity.ok(response);
    }

    // In your DepartmentController class

    @PutMapping("/{departmentId}/modules")
    public ResponseEntity<Map<String, Object>> assignModulesToDepartment(
            @PathVariable String departmentId,
            @Valid @RequestBody AssignModulesRequest request
    ) {
        departmentService.assignModulesToDepartment(departmentId, request);
        return ResponseEntity.ok(Map.of(
                "message", "Modules assigned to department successfully!"
        ));
    }

    @GetMapping("/{departmentId}/modules")
    public ResponseEntity<List<SimpleModule>> getAssignedModulesForDepartment(
            @PathVariable String departmentId
    ) {
        List<SimpleModule> modules = departmentService.getAssignedModules(departmentId);
        return ResponseEntity.ok(modules);
    }


}
