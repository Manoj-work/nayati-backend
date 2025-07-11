package com.medhir.rest.dto.rbac.roles;

import com.medhir.rest.model.rbac.ModulePermission;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRoleRequest {
    private String name;
    private List<ModulePermission> permissions;
}
