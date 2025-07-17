package com.medhir.rest.service.auth;

import com.medhir.rest.repository.auth.EmployeeRoleRepository;
import com.medhir.rest.model.employee.EmployeeModel;
import com.medhir.rest.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeRoleService {

    private final EmployeeRoleRepository employeeRoleRepository;

    public Set<String> getEmployeeRoles(String employeeId) {
        return employeeRoleRepository.findByEmployeeId(employeeId)
                .map(EmployeeModel::getRoles)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }
} 