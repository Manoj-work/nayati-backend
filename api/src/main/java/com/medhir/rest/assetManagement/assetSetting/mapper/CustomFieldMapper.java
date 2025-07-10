package com.medhir.rest.assetManagement.assetSetting.mapper;

import com.medhir.rest.assetManagement.assetSetting.model.CustomField;
import com.medhir.rest.assetManagement.assetSetting.dto.CustomFieldDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomFieldMapper {
    
    @Mapping(target = "id", ignore = true)
    CustomField toEntity(CustomFieldDTO dto);
    
    CustomFieldDTO toDTO(CustomField entity);
    
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(CustomFieldDTO dto, @MappingTarget CustomField entity);
} 