package com.medhir.rest.dto.auth;

import lombok.Data;
 
@Data
public class LoginRequest {
    private String email;
    private String password; // This will be the phone number
} 