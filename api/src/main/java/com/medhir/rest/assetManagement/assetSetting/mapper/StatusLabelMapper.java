package com.medhir.rest.assetManagement.assetSetting.mapper;

import com.medhir.rest.assetManagement.assetSetting.model.StatusLabel;
import com.medhir.rest.assetManagement.assetSetting.dto.StatusLabelDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StatusLabelMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statusLabelId", ignore = true)
    StatusLabel toEntity(StatusLabelDTO dto);
    
    StatusLabelDTO toDTO(StatusLabel statusLabel);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statusLabelId", ignore = true)
    void updateEntityFromDTO(StatusLabelDTO dto, @MappingTarget StatusLabel statusLabel);
} 