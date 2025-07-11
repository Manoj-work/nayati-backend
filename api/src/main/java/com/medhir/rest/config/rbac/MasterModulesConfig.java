package com.medhir.rest.config.rbac;

import lombok.Data;

import java.util.List;

@Data
public class MasterModulesConfig {
    private List<Module> modules;

    @Data
    public static class Module {
        private String moduleId;
        private String moduleName;
        private List<Feature> features;
    }

    @Data
    public static class Feature {
        private String featureId;
        private String featureName;
        private List<SubFeature> subFeatures;
    }

    @Data
    public static class SubFeature {
        private String subFeatureId;
        private String subFeatureName;
        private List<String> actions; // e.g. CREATE, READ, UPDATE, DELETE
    }
}
