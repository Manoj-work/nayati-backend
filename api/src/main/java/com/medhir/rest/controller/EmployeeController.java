package com.medhir.rest.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.medhir.rest.dto.CompanyEmployeeDTO;
import com.medhir.rest.dto.ManagerEmployeeDTO;
import com.medhir.rest.dto.UpdateEmployeeRoles;
import com.medhir.rest.dto.UserCompanyDTO;
import com.medhir.rest.dto.EmployeeAttendanceDetailsDTO;
import com.medhir.rest.dto.EmployeeWithLeaveDetailsDTO;
import com.medhir.rest.dto.EmployeeLeavePolicyWeeklyOffsDTO;
import com.medhir.rest.dto.EmployeeDTO;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.employee.EmployeeModel;
import com.medhir.rest.repository.ModuleRepository;
import com.medhir.rest.service.EmployeeService;
import com.medhir.rest.utils.GeneratedId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

@RestController
@RequestMapping("/")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private GeneratedId generatedId;

    @Autowired
    private ModuleRepository moduleRepository;



    // Get all companies associated with admins
    @GetMapping("/hradmin/companies/{employeeId}")
    public ResponseEntity<List<UserCompanyDTO>> getCompaniesForAdmin(@PathVariable String employeeId) {
        List<UserCompanyDTO> companies = employeeService.getEmployeeCompanies(employeeId);
        return ResponseEntity.ok(companies);
    }
    // Get all companies associated with admins
    @GetMapping("/employee/companies/{employeeId}")
    public ResponseEntity<List<UserCompanyDTO>> getUserCompanies(@PathVariable String employeeId) {
        List<UserCompanyDTO> companies = employeeService.getEmployeeCompanies(employeeId);
        return ResponseEntity.ok(companies);
    }

    @PostMapping("/hradmin/employees")
    public ResponseEntity<Map<String, Object>> createEmployee(
            @RequestParam("employee") String employeeJson, // Receive employee as JSON string
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "aadharImage", required = false) MultipartFile aadharImage,
            @RequestParam(value = "panImage", required = false) MultipartFile panImage,
            @RequestParam(value = "passportImage", required = false) MultipartFile passportImage,
            @RequestParam(value = "drivingLicenseImage", required = false) MultipartFile drivingLicenseImage,
            @RequestParam(value = "voterIdImage", required = false) MultipartFile voterIdImage,
            @RequestParam(value = "passbookImage", required = false) MultipartFile passbookImage) throws Exception {

        // Convert JSON string to EmployeeDTO object
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Required for LocalDate
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        EmployeeDTO employeeDTO = objectMapper.readValue(employeeJson, EmployeeDTO.class);

        // Validate the DTO
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<EmployeeDTO>> violations = validator.validate(employeeDTO);

        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<EmployeeDTO> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        // Pass the DTO directly to the service layer
        EmployeeWithLeaveDetailsDTO createdEmployee = employeeService.createEmployee(
                employeeDTO, profileImage, aadharImage, panImage, passportImage, drivingLicenseImage, voterIdImage, passbookImage);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Employee created successfully");
        response.put("employee", createdEmployee);
        return ResponseEntity.ok(response);
    }

    // Get All Employees
    @GetMapping("/employee")
    public ResponseEntity<List<EmployeeWithLeaveDetailsDTO>> getAllEmployees(){
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // Get All Employees (HR Admin endpoint)
    @GetMapping("/hradmin/employees")
    public ResponseEntity<List<EmployeeWithLeaveDetailsDTO>> getAllEmployeesHRAdmin(){
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // Get All Employees with minimal fields (name and employeeId)
    @GetMapping("/employees/minimal")
    public ResponseEntity<List<Map<String, String>>> getAllEmployeesMinimal() {
        return ResponseEntity.ok(employeeService.getAllEmployeesMinimal());
    }



    // Get All Employees by Company ID with additional details
    @GetMapping("/hradmin/companies/{companyId}/employees")
    public ResponseEntity<List<CompanyEmployeeDTO>> getAllEmployeesByCompanyIdWithDetails(@PathVariable String companyId) {
        return ResponseEntity.ok(employeeService.getAllEmployeesByCompanyIdWithDetails(companyId));
    }

    // Get Employee by Employee ID
    @GetMapping("/employee/id/{employeeId}")
    public ResponseEntity<Optional<EmployeeWithLeaveDetailsDTO>> getEmployeeById(@PathVariable String employeeId){
        return ResponseEntity.ok(employeeService.getEmployeeById(employeeId));
    }

    @PutMapping("/hradmin/employees/{employeeId}")
    public ResponseEntity<Map<String, Object>> updateEmployee(
            @PathVariable String employeeId,
            @RequestParam("employee") String employeeJson, // Receive employee as JSON string
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "aadharImage", required = false) MultipartFile aadharImage,
            @RequestParam(value = "panImage", required = false) MultipartFile panImage,
            @RequestParam(value = "passportImage", required = false) MultipartFile passportImage,
            @RequestParam(value = "drivingLicenseImage", required = false) MultipartFile drivingLicenseImage,
            @RequestParam(value = "voterIdImage", required = false) MultipartFile voterIdImage,
            @RequestParam(value = "passbookImage", required = false) MultipartFile passbookImage) throws Exception {

        // Convert JSON string to EmployeeDTO object
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Required for LocalDate
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        EmployeeDTO employeeDTO = objectMapper.readValue(employeeJson, EmployeeDTO.class);

        // Validate the DTO
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<EmployeeDTO>> violations = validator.validate(employeeDTO);

        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<EmployeeDTO> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        // Pass the DTO directly to the service layer
        EmployeeWithLeaveDetailsDTO updatedEmployee = employeeService.updateEmployee(
                employeeId, employeeDTO, profileImage, aadharImage, panImage, passportImage, drivingLicenseImage, voterIdImage, passbookImage);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Employee updated successfully");
        response.put("employee", updatedEmployee);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employees/manager/{managerId}")
    public ResponseEntity<List<ManagerEmployeeDTO>> getEmployeesByManagerId(@PathVariable String managerId){
        List<ManagerEmployeeDTO> employees = employeeService.getEmployeesByManager(managerId);
        return ResponseEntity.ok(employees);
    }

    // Delete Employee by Employee ID
    @DeleteMapping("/hradmin/employees/{employeeId}")
    public ResponseEntity<Map<String, String>> deleteEmployee(
            @PathVariable String employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok(Map.of("message", "Employee deleted successfully"));
    }

    @GetMapping("/departments/{departmentId}/managers")
    public ResponseEntity<List<Map<String, String>>> getManagersByDepartment(
            @PathVariable String departmentId) {
        return ResponseEntity.ok(employeeService.getManagersByDepartment(departmentId));
    }

    @PutMapping("/hradmin/employees/{employeeId}/roles")
    public ResponseEntity<Map<String, Object>> updateEmployeeRole(
            @PathVariable String employeeId,
            @RequestBody UpdateEmployeeRoles request) {

        EmployeeModel updatedEmployee = employeeService.updateEmployeeRole(
                employeeId,
                request.getRoles(),
                request.getOperation(),
                request.getCompanyId()
        );

        return ResponseEntity.ok(Map.of(
                "message", "Employee roles updated successfully"
        ));
    }

    @GetMapping("/employee/{employeeId}/attendance-details")
    public ResponseEntity<EmployeeAttendanceDetailsDTO> getEmployeeAttendanceDetails(@PathVariable String employeeId) {
        return ResponseEntity.ok(employeeService.getEmployeeAttendanceDetails(employeeId));
    }

    @GetMapping("/hradmin/employees/{employeeId}/roles")
    public ResponseEntity<Map<String, Object>> getEmployeeRolesWithModules(@PathVariable String employeeId) {
        EmployeeModel employee = employeeService.getEmployeeById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        // Get employee's roles
        Set<String> roles = employee.getRoles();
        if (roles == null) {
            roles = new HashSet<>();
        }

        // Get employee's modules
        List<String> moduleIds = employee.getModuleIds();
        List<Map<String, String>> moduleDetails = new ArrayList<>();
        
        if (moduleIds != null) {
            for (String moduleId : moduleIds) {
                moduleRepository.findByModuleId(moduleId).ifPresent(module -> {
                    Map<String, String> moduleInfo = new HashMap<>();
                    moduleInfo.put("moduleId", module.getModuleId());
                    moduleInfo.put("moduleName", module.getModuleName());
                    moduleInfo.put("description", module.getDescription());
                    moduleDetails.add(moduleInfo);
                });
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employeeId);
        response.put("employeeName", employee.getName());
        response.put("roles", roles);
        response.put("modules", moduleDetails);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}/leave-policy")
    public ResponseEntity<EmployeeLeavePolicyWeeklyOffsDTO> getEmployeeLeavePolicy(@PathVariable String employeeId) {
        return ResponseEntity.ok(employeeService.getEmployeeLeavePolicy(employeeId));
    }
    
}
