package com.medhir.rest.dto.rbac;

import lombok.Data;
import java.util.List;

@Data
public class AssignModulesRequest {

    private List<ModuleRequest> modules;

    @Data
    public static class ModuleRequest {
        private String moduleId;
        private String moduleName;
        private List<FeatureRequest> features;
    }

    @Data
    public static class FeatureRequest {
        private String featureId;
        private String featureName;
        private List<SubFeatureRequest> subFeatures;
    }

    @Data
    public static class SubFeatureRequest {
        private String subFeatureId;
        private String subFeatureName;
        private List<String> actions;
    }
}
