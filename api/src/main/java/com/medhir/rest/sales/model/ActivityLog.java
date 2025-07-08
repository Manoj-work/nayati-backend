package com.medhir.rest.sales.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {
    private String id;
    private String action;
    private String details;
    private String user;
    private String timestamp;
    private String activityType;
    private Map<String, Object> metadata = new HashMap<>();
} 