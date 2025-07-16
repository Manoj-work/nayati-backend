package com.medhir.rest.model.rbac;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubFeaturePermission {
    private String subFeatureId;
    private String subFeatureName;
    private List<String> actions; // Example: CREATE, READ, UPDATE, DELETE
}