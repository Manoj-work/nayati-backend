package com.medhir.rest.mapper.rbac;

import com.medhir.rest.dto.rbac.AssignModulesRequest;
import com.medhir.rest.model.rbac.FeaturePermission;
import com.medhir.rest.model.rbac.ModulePermission;
import com.medhir.rest.model.rbac.SubFeaturePermission;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssignModulesMapper {

    AssignModulesMapper INSTANCE = Mappers.getMapper(AssignModulesMapper.class);

    List<ModulePermission> toModulePermissions(List<AssignModulesRequest.ModuleRequest> moduleRequests);

    FeaturePermission toFeaturePermission(AssignModulesRequest.FeatureRequest featureRequest);

    SubFeaturePermission toSubFeaturePermission(AssignModulesRequest.SubFeatureRequest subFeatureRequest);

}
