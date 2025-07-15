package com.medhir.rest.dto.rbac.roles;

import com.medhir.rest.model.rbac.ModulePermission;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateRoleRequest {
    @NotNull private String roleName;
    @NotNull private List<ModulePermission> permissions;
    @NotNull private String companyId;
}
