package com.medhir.rest.mapper.rbac.roles;

import com.medhir.rest.dto.rbac.roles.CreateRoleRequest;
import com.medhir.rest.dto.rbac.roles.UpdateRoleRequest;
import com.medhir.rest.model.rbac.Roles;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RolesMapper {

    Roles toRoles(CreateRoleRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRolesFromRequest(UpdateRoleRequest request, @MappingTarget Roles roles);

}
