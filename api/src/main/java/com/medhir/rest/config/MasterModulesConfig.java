package com.medhir.rest.config;

import lombok.Data;

import java.util.List;

@Data
public class MasterModulesConfig {
    private List<Module> modules;

    @Data
    public static class Module {
        private String id;
        private String name;
        private List<Feature> features;
    }

    @Data
    public static class Feature {
        private String id;
        private String name;
    }
}

