package com.medhir.rest.dto.rbac;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleModule {
    private String moduleId;
    private String moduleName;
}

