package com.medhir.rest.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private List<String> roles;
    private String employeeId;
    private boolean isPasswordChanged;
    private String departmentName;
} 