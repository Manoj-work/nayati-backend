package com.medhir.rest.service.settings;

import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.settings.DepartmentModel;
import com.medhir.rest.service.CompanyService;
import com.medhir.rest.repository.settings.DepartmentRepository;
import com.medhir.rest.utils.GeneratedId;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private LeavePolicyService leavePolicyService;


    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    private CompanyService companyService;

    public DepartmentModel createDepartment(DepartmentModel department) {
        // Check if company exists
        companyService.getCompanyById(department.getCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + department.getCompanyId()));

        if (departmentRepository.existsByNameAndCompanyId(department.getName(), department.getCompanyId())) {
            throw new DuplicateResourceException("Department with name " + department.getName() + " already exists in this company");
        }

        // Verify leave policy exists
        //     leavePolicyService.getLeavePolicyById(department.getLeavePolicy());
        if (department.getLeavePolicy() != null) {
            leavePolicyService.getLeavePolicyById(department.getLeavePolicy());
        }

        String newDepartmentId = "DEPT" + snowflakeIdGenerator.nextId();
        department.setDepartmentId(newDepartmentId);

        department.setCreatedAt(LocalDateTime.now().toString());
        department.setUpdatedAt(LocalDateTime.now().toString());
        return departmentRepository.save(department);
    }

    public List<DepartmentModel> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public List<DepartmentModel> getDepartmentsByCompanyId(String companyId) {
        return departmentRepository.findByCompanyId(companyId);
    }

    public DepartmentModel getDepartmentById(String id) {
        // First try to find by departmentId
        DepartmentModel department = departmentRepository.findByDepartmentId(id)
                .orElse(null);

        // If not found by departmentId, try by MongoDB id
        if (department == null) {
            department = departmentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        }

        return department;
    }

    public DepartmentModel updateDepartment(String id, DepartmentModel department) {
        DepartmentModel existingDepartment = getDepartmentById(id);
        
        // Check if company exists if companyId is being updated
        if (department.getCompanyId() != null && !department.getCompanyId().equals(existingDepartment.getCompanyId())) {
            companyService.getCompanyById(department.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + department.getCompanyId()));
        }
        
        if (!existingDepartment.getName().equals(department.getName()) && 
            departmentRepository.existsByNameAndCompanyId(department.getName(), department.getCompanyId())) {
            throw new DuplicateResourceException("Department with name " + department.getName() + " already exists in this company");
        }

        // Only validate leave policy if it's provided
        if (department.getLeavePolicy() != null) {
            leavePolicyService.getLeavePolicyById(department.getLeavePolicy());
        }

        //  Update all fields
        if (department.getName() != null) {
            existingDepartment.setName(department.getName());
        }

        existingDepartment.setName(department.getName());
        existingDepartment.setDescription(department.getDescription());

        existingDepartment.setLeavePolicy(department.getLeavePolicy());
        existingDepartment.setWeeklyHolidays(department.getWeeklyHolidays());
        existingDepartment.setUpdatedAt(LocalDateTime.now().toString());
        if(department.getLeavePolicy() != null){
            existingDepartment.setLeavePolicy(department.getLeavePolicy());
        }
        // Update companyId if provided
        if (department.getCompanyId() != null) {
            existingDepartment.setCompanyId(department.getCompanyId());
        }

        return departmentRepository.save(existingDepartment);
    }

    public void deleteDepartment(String id) {
        DepartmentModel department = getDepartmentById(id);
        departmentRepository.deleteById(department.getId());
    }

    public List<DepartmentModel> getDepartmentsByIds(Set<String> ids) {
        return departmentRepository.findByDepartmentIdIn(ids);
    }
}
