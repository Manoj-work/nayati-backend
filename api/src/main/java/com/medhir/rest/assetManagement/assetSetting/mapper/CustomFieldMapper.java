package com.medhir.rest.assetManagement.assetSetting.mapper;

import com.medhir.rest.assetManagement.assetSetting.model.CustomField;
import com.medhir.rest.assetManagement.assetSetting.dto.CustomFieldDTO;
import org.springframework.stereotype.Component;

@Component
public class CustomFieldMapper {
    
    public CustomField toEntity(CustomFieldDTO dto) {
        if (dto == null) {
            return null;
        }
        
        CustomField entity = new CustomField();
        entity.setCategoryId(dto.getCategoryId());
        entity.setLabel(dto.getLabel());
        entity.setType(dto.getType());
        entity.setRequired(dto.isRequired());
        entity.setEnabled(dto.isEnabled());
        return entity;
    }
    
    public CustomFieldDTO toDTO(CustomField entity) {
        if (entity == null) {
            return null;
        }
        
        CustomFieldDTO dto = new CustomFieldDTO();
        dto.setId(entity.getId());
        dto.setCategoryId(entity.getCategoryId());
        dto.setLabel(entity.getLabel());
        dto.setType(entity.getType());
        dto.setRequired(entity.isRequired());
        dto.setEnabled(entity.isEnabled());
        return dto;
    }
    
    public void updateEntityFromDTO(CustomFieldDTO dto, CustomField entity) {
        if (dto == null || entity == null) {
            return;
        }
        
        entity.setCategoryId(dto.getCategoryId());
        entity.setLabel(dto.getLabel());
        entity.setType(dto.getType());
        entity.setRequired(dto.isRequired());
        entity.setEnabled(dto.isEnabled());
    }
} 