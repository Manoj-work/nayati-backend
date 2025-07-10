package com.medhir.rest.assetManagement.assetSetting.mapper;

import com.medhir.rest.assetManagement.assetSetting.model.Location;
import com.medhir.rest.assetManagement.assetSetting.dto.LocationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "locationId", ignore = true)
    Location toEntity(LocationDTO dto);
    
    LocationDTO toDTO(Location location);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "locationId", ignore = true)
    void updateEntityFromDTO(LocationDTO dto, @MappingTarget Location location);
} 