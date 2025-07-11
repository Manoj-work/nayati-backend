package com.medhir.rest.model.rbac;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeaturePermission {
    private String featureId;
    private String featureName;
    private List<SubFeaturePermission> subFeatures;
}
