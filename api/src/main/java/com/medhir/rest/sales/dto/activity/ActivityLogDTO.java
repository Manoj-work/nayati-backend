package com.medhir.rest.sales.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDTO {
    @NotBlank(message = "Log ID is required")
    private String id;
    @NotBlank(message = "Action is required")
    private String action;
    private String details;
    @NotBlank(message = "User is required")
    private String user;
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$", message = "Timestamp must be in yyyy-MM-dd'T'HH:mm:ss format")
    private String timestamp;
    private String activityType = "USER_ACTION";
    private Map<String, Object> metadata = new HashMap<>();
} 