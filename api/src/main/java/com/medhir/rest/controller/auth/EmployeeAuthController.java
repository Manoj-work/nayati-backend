package com.medhir.rest.controller.auth;

import com.medhir.rest.dto.auth.LoginRequest;
import com.medhir.rest.dto.auth.LoginResponse;
import com.medhir.rest.dto.auth.PasswordChangeRequest;
import com.medhir.rest.service.auth.EmployeeAuthService;
import com.medhir.rest.service.auth.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmployeeAuthController {

    private final EmployeeAuthService employeeAuthService;
    private final PasswordService passwordService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = employeeAuthService.authenticate(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("password/change")
    public ResponseEntity<Map<String, String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody PasswordChangeRequest request) {
        String email = authentication.getName();
        passwordService.changePassword(email, request);
        return ResponseEntity.ok(Map.of("message", "Password has been changed successfully"));
    }

    @GetMapping("password/status")
    public ResponseEntity<Boolean> getPasswordStatus(Authentication authentication) {
        String email = authentication.getName();
        boolean isChanged = passwordService.isPasswordChanged(email);
        return ResponseEntity.ok(isChanged);
    }

} 