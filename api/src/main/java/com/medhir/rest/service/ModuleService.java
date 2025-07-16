package com.medhir.rest.service;

import com.medhir.rest.dto.ModuleResponseDTO;
import com.medhir.rest.model.EmployeeModel;
import com.medhir.rest.repository.EmployeeRepository;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.CompanyModel;
import com.medhir.rest.model.ModuleModel;
import com.medhir.rest.repository.ModuleRepository;
import com.medhir.rest.service.company.CompanyService;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompanyService companyService;

//    @Autowired
//    private GeneratedId generatedId;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    public ModuleModel createModule(ModuleModel moduleModel) {
        // Validate company exists
        if (moduleModel.getCompanyId() != null) {
            companyService.getCompanyById(moduleModel.getCompanyId());
        }

        // Validate all employees exist
        List<EmployeeModel> employees = new ArrayList<>();
        for (String employeeId : moduleModel.getEmployeeIds()) {
            EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + employeeId + " not found"));
            employees.add(employee);
        }

        // Generate module ID
//        moduleModel.setModuleId(generatedId.generateId("MID", ModuleModel.class, "moduleId"));
        moduleModel.setModuleId("MOD" + snowflakeIdGenerator.nextId());

        // Save the module
        ModuleModel savedModule = moduleRepository.save(moduleModel);

        // Update employee details with module ID and roles
        for (EmployeeModel employee : employees) {
            if (employee.getModuleIds() == null) {
                employee.setModuleIds(new ArrayList<>());
            }
            employee.getModuleIds().add(savedModule.getModuleId());

            String moduleNameUpper = savedModule.getModuleName().toUpperCase();
            if (moduleNameUpper.contains("HR") || moduleNameUpper.contains("HUMAN RESOURCE") || moduleNameUpper.contains("HUMANRESOURCE")) {
                Set<String> roles = employee.getRoles();
                if (roles == null) {
                    roles = new HashSet<>();
                }
                roles.add("HRADMIN");
                employee.setRoles(roles);
            }
            if (savedModule.getModuleName().toUpperCase().contains("ACCOUNT")) {
                Set<String> roles = employee.getRoles();
                if (roles == null) {
                    roles = new HashSet<>();
                }
                roles.add("ACCOUNTANT");
                employee.setRoles(roles);
            }
            if (savedModule.getModuleName().toUpperCase().contains("PROJECT")) {
                Set<String> roles = employee.getRoles();
                if (roles == null) {
                    roles = new HashSet<>();
                }
                roles.add("PROJECTMANAGER");
                employee.setRoles(roles);
            }
            if (savedModule.getModuleName().toUpperCase().contains("SALE")) {
                Set<String> roles = employee.getRoles();
                if (roles == null) {
                    roles = new HashSet<>();
                }
                roles.add("SALES");
                employee.setRoles(roles);
            }
            
            employeeRepository.save(employee);
        }

        return savedModule;
    }

    public List<ModuleResponseDTO> getAllModules() {
        List<ModuleModel> modules = moduleRepository.findAll();
        return modules.stream().map(module -> {
            List<Map<String, String>> employees = new ArrayList<>();
            if (module.getEmployeeIds() != null) {
                employees = module.getEmployeeIds().stream()
                        .map(employeeId -> {
                            Map<String, String> employeeInfo = new HashMap<>();
                            employeeInfo.put("employeeId", employeeId);
                            employeeInfo.put("name", employeeRepository.findByEmployeeId(employeeId)
                                    .map(EmployeeModel::getName)
                                    .orElse("Unknown Employee"));
                            return employeeInfo;
                        })
                        .collect(Collectors.toList());
            }

            Map<String, String> companyInfo = new HashMap<>();
            if (module.getCompanyId() != null) {
                try {
                    Optional<CompanyModel> company = companyService.getCompanyById(module.getCompanyId());
                    companyInfo.put("companyId", module.getCompanyId());
                    companyInfo.put("name", company.get().getName());
                } catch (ResourceNotFoundException e) {
                    companyInfo.put("companyId", module.getCompanyId());
                    companyInfo.put("name", "Unknown Company");
                }
            }

            return new ModuleResponseDTO(
                    module.getModuleId(),
                    module.getModuleName(),
                    module.getDescription(),
                    employees,
                    companyInfo
            );
        }).collect(Collectors.toList());
    }

    public void updateModule(String moduleId, ModuleModel updatedModule) {
        ModuleModel existingModule = moduleRepository.findByModuleId(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module with ID " + moduleId + " not found"));

        // Store the old employee IDs for comparison
        List<String> oldEmployeeIds = existingModule.getEmployeeIds() != null ? new ArrayList<>(existingModule.getEmployeeIds()) : new ArrayList<>();
        List<String> newEmployeeIds = updatedModule.getEmployeeIds() != null ? new ArrayList<>(updatedModule.getEmployeeIds()) : new ArrayList<>();

        // Update only the allowed fields
        if (updatedModule.getModuleName() != null) {
            existingModule.setModuleName(updatedModule.getModuleName());
        }
        if (updatedModule.getDescription() != null) {
            existingModule.setDescription(updatedModule.getDescription());
        }
        if (updatedModule.getCompanyId() != null) {
            // Validate company exists
            companyService.getCompanyById(updatedModule.getCompanyId());
            existingModule.setCompanyId(updatedModule.getCompanyId());
        }

        // Handle employee updates
        if (updatedModule.getEmployeeIds() != null) {
            // Employees to remove: in old but not in new
            List<String> removedEmployeeIds = oldEmployeeIds.stream()
                    .filter(id -> !newEmployeeIds.contains(id))
                    .collect(Collectors.toList());
            // Employees to add: in new but not in old
            List<String> addedEmployeeIds = newEmployeeIds.stream()
                    .filter(id -> !oldEmployeeIds.contains(id))
                    .collect(Collectors.toList());

            // Remove module ID from old employees who are no longer in the list
            for (String oldEmployeeId : removedEmployeeIds) {
                employeeRepository.findByEmployeeId(oldEmployeeId).ifPresent(employee -> {
                    if (employee.getModuleIds() != null) {
                        employee.getModuleIds().remove(moduleId);
                    }
                    // Remove roles if not present in any other relevant module
                    Set<String> roles = employee.getRoles();
                    if (roles != null) {
                        // Check for each role
                        removeRoleIfNoOtherModule(employee, moduleId, existingModule.getModuleName(), roles);
                        employee.setRoles(roles);
                    }
                    employeeRepository.save(employee);
                });
            }

            // Validate all new employees exist and update their module associations
            for (String employeeId : addedEmployeeIds) {
                EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + employeeId + " not found"));
                // Add module ID to employee if not already present
                if (employee.getModuleIds() == null) {
                    employee.setModuleIds(new ArrayList<>());
                }
                if (!employee.getModuleIds().contains(moduleId)) {
                    employee.getModuleIds().add(moduleId);
                }
                // Add roles based on module name
                Set<String> roles = employee.getRoles();
                if (roles == null) {
                    roles = new HashSet<>();
                }
                String moduleName = existingModule.getModuleName().toUpperCase();
                if (moduleName.contains("HR") || moduleName.contains("HUMAN RESOURCE") || moduleName.contains("HUMANRESOURCE")) {
                    roles.add("HRADMIN");
                }
                if (moduleName.contains("ACCOUNT")) {
                    roles.add("ACCOUNTANT");
                }
                if (moduleName.contains("PROJECT")) {
                    roles.add("PROJECTMANAGER");
                }
                if (moduleName.contains("SALE")) {
                    roles.add("SALES");
                }
                employee.setRoles(roles);
                employeeRepository.save(employee);
            }
            // For employees that remain, ensure their roles are correct if module name changed
            List<String> remainingEmployeeIds = newEmployeeIds.stream()
                    .filter(oldEmployeeIds::contains)
                    .collect(Collectors.toList());
            for (String employeeId : remainingEmployeeIds) {
                EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + employeeId + " not found"));
                Set<String> roles = employee.getRoles();
                if (roles == null) {
                    roles = new HashSet<>();
                }
                String moduleName = existingModule.getModuleName().toUpperCase();
                if (moduleName.contains("HR")) {
                    roles.add("HRADMIN");
                } else {
                    roles.remove("HRADMIN");
                }
                if (moduleName.contains("ACCOUNT")) {
                    roles.add("ACCOUNTANT");
                } else {
                    roles.remove("ACCOUNTANT");
                }
                if (moduleName.contains("PROJECT")) {
                    roles.add("PROJECTMANAGER");
                } else {
                    roles.remove("PROJECTMANAGER");
                }
                employee.setRoles(roles);
                employeeRepository.save(employee);
            }
            existingModule.setEmployeeIds(newEmployeeIds);
        }

        moduleRepository.save(existingModule);
    }

    // Helper method to remove roles if employee is not in any other module with that role
    private void removeRoleIfNoOtherModule(EmployeeModel employee, String removedModuleId, String removedModuleName, Set<String> roles) {
        List<String> otherModuleIds = employee.getModuleIds();
        if (otherModuleIds == null) return;
        // Fetch all other modules
        List<ModuleModel> otherModules = moduleRepository.findAllById(otherModuleIds);
        boolean hasHR = false, hasAccount = false, hasProject = false;
        for (ModuleModel m : otherModules) {
            String name = m.getModuleName().toUpperCase();
            if (name.contains("HR")) hasHR = true;
            if (name.contains("ACCOUNT")) hasAccount = true;
            if (name.contains("PROJECT")) hasProject = true;
        }
        String removedName = removedModuleName.toUpperCase();
        if (removedName.contains("HR") && !hasHR) {
            roles.remove("HRADMIN");
        }
        if (removedName.contains("ACCOUNT") && !hasAccount) {
            roles.remove("ACCOUNTANT");
        }
        if (removedName.contains("PROJECT") && !hasProject) {
            roles.remove("PROJECTMANAGER");
        }
    }

    public void deleteModule(String moduleId) {
        ModuleModel module = moduleRepository.findByModuleId(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module with ID " + moduleId + " not found"));

        // Remove module ID from all associated employees
        if (module.getEmployeeIds() != null) {
            for (String employeeId : module.getEmployeeIds()) {
                employeeRepository.findByEmployeeId(employeeId).ifPresent(employee -> {
                    if (employee.getModuleIds() != null) {
                        employee.getModuleIds().remove(moduleId);
                        // If moduleIds is now empty and employee has HRADMIN role, remove HRADMIN role
                        if (employee.getModuleIds().isEmpty() && employee.getRoles() != null && employee.getRoles().contains("HRADMIN")) {
                            employee.getRoles().remove("HRADMIN");
                        }
                        employeeRepository.save(employee);
                    }
                });
            }
        }

        moduleRepository.delete(module);
    }


}