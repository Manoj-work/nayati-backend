package com.medhir.rest.model.rbac;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModulePermission {
    private String moduleId;
    private String moduleName;
    private List<FeaturePermission> features;
}
