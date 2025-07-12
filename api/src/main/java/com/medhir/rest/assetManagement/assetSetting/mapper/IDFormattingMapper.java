package com.medhir.rest.assetManagement.assetSetting.mapper;

import com.medhir.rest.assetManagement.assetSetting.model.IDFormatting;
import com.medhir.rest.assetManagement.assetSetting.dto.IDFormattingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IDFormattingMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idFormattingId", ignore = true)
    @Mapping(target = "currentNumber", source = "startNumber")
    IDFormatting toEntity(IDFormattingDTO dto);
    
    IDFormattingDTO toDTO(IDFormatting entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idFormattingId", ignore = true)
    @Mapping(target = "currentNumber", ignore = true) // Don't update currentNumber when updating format
    void updateEntityFromDTO(IDFormattingDTO dto, @MappingTarget IDFormatting entity);
} 