package com.medhir.rest.service.auth;

import com.medhir.rest.dto.auth.LoginRequest;
import com.medhir.rest.dto.auth.LoginResponse;
import com.medhir.rest.model.auth.EmployeeAuth;
import com.medhir.rest.repository.auth.EmployeeAuthRepository;
import com.medhir.rest.config.JwtUtil;
import com.medhir.rest.model.employee.EmployeeModel;
import com.medhir.rest.repository.employee.EmployeeRepository;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.service.company.CompanyService;
import com.medhir.rest.service.settings.DepartmentService;
import com.medhir.rest.service.settings.DesignationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeAuthService implements UserDetailsService {
    private final EmployeeAuthRepository employeeAuthRepository;
    private final EmployeeRoleService employeeRoleService;
    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;
    private final CompanyService companyService;
    private final DepartmentService departmentService;
    @Autowired
    private DesignationService designationService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return employeeAuthRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public EmployeeAuth registerEmployee(String employeeId, String email, String phone) {
        // Check if employee already registered
        if (employeeAuthRepository.findByEmployeeId(employeeId).isPresent() ||
            employeeAuthRepository.findByEmail(email).isPresent()) {
            throw new ResourceNotFoundException("Employee already registered");
        }

        EmployeeAuth employeeAuth = new EmployeeAuth();
        employeeAuth.setEmployeeId(employeeId);
        employeeAuth.setEmail(email);
        employeeAuth.setPassword(passwordEncoder.encode(phone)); // Use phone number as password

        return employeeAuthRepository.save(employeeAuth);
    }

    public EmployeeAuth findByEmail(String email) {
        return employeeAuthRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No auth record found for email: " + email));
    }

    private String normalizePhoneNumber(String phone) {
        if (phone == null) {
            return "";
        }
        // Remove any non-digit characters and trim
        return phone.replaceAll("[^0-9]", "").trim();
    }

    public LoginResponse authenticate(LoginRequest request) {
        log.info("Attempting authentication for email: {}", request.getEmail());
        
        // Find auth record by email
        EmployeeAuth employeeAuth = employeeAuthRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("No auth record found for email: {}", request.getEmail());
                    throw new RuntimeException("No auth record found for email: " + request.getEmail());
                });

        // Get employee details
        EmployeeModel employee = employeeRepository.findByEmployeeId(employeeAuth.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // If password has been changed, verify against stored password
        if (employeeAuth.isPasswordChanged()) {
            if (!passwordEncoder.matches(request.getPassword(), employeeAuth.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }
        } else {
            // If password hasn't been changed, verify against phone number
            String normalizedInputPhone = normalizePhoneNumber(request.getPassword());
            String normalizedStoredPhone = normalizePhoneNumber(employee.getPhone());
            
            if (!normalizedInputPhone.equals(normalizedStoredPhone)) {
                throw new RuntimeException("Invalid credentials. Expected phone number: " + normalizedStoredPhone);
            }
        }

        // Fetch department and moduleIds
        List<String> moduleIds = new ArrayList<>();
        String departmentName = "";
        if (employee.getDepartment() != null && !employee.getDepartment().isEmpty()) {

                var department = departmentService.getDepartmentById(employee.getDepartment());
                departmentName = department.getName();
                if (department.getAssignedModules() != null) {
                    department.getAssignedModules().forEach(am -> {
                        if (am.getModuleId() != null && !am.getModuleId().isEmpty()) {
                            moduleIds.add(am.getModuleId());
                        }
                    });
                }

        }

        // Fetch designation and roles
        List<String> roleList = new ArrayList<>();
        if (employee.getDesignation() != null && !employee.getDesignation().isEmpty()) {

                var designation = designationService.getDesignationById(employee.getDesignation());
                if (designation.isManager()) {
                    roleList.add("MANAGER");
                }
                if (designation.isAdmin()) {
                    roleList.add("ADMIN");
                }
                if (!designation.isManager() && !designation.isAdmin()) {
                    roleList.add(designation.getName().toUpperCase());
                }

        } else {
            roleList.add("EMPLOYEE");
        }

        // Add previous roles from employee.getRoles()
        if (employee.getRoles() != null) {
            for (String prevRole : employee.getRoles()) {
                if (prevRole != null && !prevRole.trim().isEmpty() && !roleList.contains(prevRole)) {
                    roleList.add(prevRole);
                }
            }
        }

        log.info("Roles for employee {}: {}", employeeAuth.getEmployeeId(), roleList);
        log.info("ModuleIds for employee {}: {}", employeeAuth.getEmployeeId(), moduleIds);

        // Generate JWT token with moduleIds and roles
        String token = jwtService.generateToken(employeeAuth, moduleIds, roleList);
        log.info("JWT token generated successfully for employee: {}", employeeAuth.getEmployeeId());

        return LoginResponse.builder()
                .token(token)
                .roles(roleList)
                .employeeId(employeeAuth.getEmployeeId())
                .isPasswordChanged(employeeAuth.isPasswordChanged())
                .departmentName(departmentName)
                .moduleIds(moduleIds)
                .build();
    }

    public void createEmployeeAuth(EmployeeModel employee) {
        log.info("Creating auth record for employee: {}", employee.getEmployeeId());
        log.info("Employee phone number: {}", employee.getPhone());
        
        // Check if auth record already exists
        if (employeeAuthRepository.findByEmployeeId(employee.getEmployeeId()).isPresent()) {
            log.warn("Auth record already exists for employee: {}", employee.getEmployeeId());
            return;
        }

        // Normalize phone number before encoding
        String normalizedPhone = normalizePhoneNumber(employee.getPhone());
        log.info("Normalized phone number: {}", normalizedPhone);
        String encodedPassword = passwordEncoder.encode(normalizedPhone);
        log.info("Encoded phone number: {}", encodedPassword);

        // Get roles from employee model
        Set<String> roles = employee.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of("EMPLOYEE");
        }
        log.info("Setting roles for employee {}: {}", employee.getEmployeeId(), roles);

        EmployeeAuth employeeAuth = EmployeeAuth.builder()
                .employeeId(employee.getEmployeeId())
                .email(employee.getEmailPersonal())
                .password(encodedPassword)
                .roles(roles)
                .build();
        
        employeeAuthRepository.save(employeeAuth);
        log.info("Auth record created successfully for employee: {}", employee.getEmployeeId());
    }

    private String generateToken(String email, Set<String> roles) {
        return jwtUtil.generateTokenWithStringRoles(email, roles);
    }

    public EmployeeAuth findByEmployeeId(String employeeId) {
        return employeeAuthRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new UsernameNotFoundException("No auth record found for employee: " + employeeId));
    }

    public void delete(EmployeeAuth auth) {
        employeeAuthRepository.delete(auth);
    }
} 