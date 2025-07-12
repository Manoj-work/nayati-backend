package com.medhir.rest.assetManagement.assetSetting.mapper;

import com.medhir.rest.assetManagement.assetSetting.model.Category;
import com.medhir.rest.assetManagement.assetSetting.dto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    Category toEntity(CategoryDTO dto);
    
    CategoryDTO toDTO(Category category);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    void updateEntityFromDTO(CategoryDTO dto, @MappingTarget Category category);
} 