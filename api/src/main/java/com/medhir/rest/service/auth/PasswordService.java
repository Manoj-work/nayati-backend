package com.medhir.rest.service.auth;

import com.medhir.rest.dto.auth.PasswordChangeRequest;
import com.medhir.rest.model.auth.EmployeeAuth;
import com.medhir.rest.repository.auth.EmployeeAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final EmployeeAuthRepository employeeAuthRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(String email, PasswordChangeRequest request) {
        EmployeeAuth employee = employeeAuthRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), employee.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password and set flag
        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
        employee.setPasswordChanged(true);
        employeeAuthRepository.save(employee);
    }

    public boolean isPasswordChanged(String email) {
        return employeeAuthRepository.findByEmail(email)
                .map(EmployeeAuth::isPasswordChanged)
                .orElse(false);
    }
} 