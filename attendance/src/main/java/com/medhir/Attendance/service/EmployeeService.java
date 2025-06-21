package com.medhir.Attendance.service;

import com.medhir.Attendance.model.Employee;
import com.medhir.Attendance.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public boolean employeeExists(String empId) {
        return employeeRepository.findByEmployeeId(empId).isPresent();
    }

    public Optional<Employee> getEmployeeByEmpId(String empId) {
        return employeeRepository.findByEmployeeId(empId);
    }

    public List<Employee> getEmployeesByEmpIds(List<String> empIds) {
        return employeeRepository.findByEmployeeIdIn(empIds);
    }
}
