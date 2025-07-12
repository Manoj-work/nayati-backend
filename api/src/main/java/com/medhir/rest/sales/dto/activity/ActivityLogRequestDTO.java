package com.medhir.rest.sales.dto.activity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogRequestDTO {
    
    private String id;
    
    @NotBlank(message = "Action is required")
    private String action;
    
    @NotBlank(message = "Details are required")
    private String details;
    
    private String user;
    
    private String timestamp;
    
    private String activityType = "USER_ACTION";
    private Map<String, Object> metadata = new HashMap<>();
} 