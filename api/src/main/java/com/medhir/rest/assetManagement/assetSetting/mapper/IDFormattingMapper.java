package com.medhir.rest.assetManagement.assetSetting.mapper;

import com.medhir.rest.assetManagement.assetSetting.model.IDFormatting;
import com.medhir.rest.assetManagement.assetSetting.dto.IDFormattingDTO;
import org.springframework.stereotype.Component;

@Component
public class IDFormattingMapper {
    
    public IDFormatting toEntity(IDFormattingDTO dto) {
        if (dto == null) {
            return null;
        }
        
        IDFormatting entity = new IDFormatting();
        entity.setIdFormattingId(dto.getIdFormattingId());
        entity.setCategoryId(dto.getCategoryId());
        entity.setPrefix(dto.getPrefix());
        entity.setObjectId(dto.getObjectId());
        entity.setStartNumber(dto.getStartNumber());
        entity.setCurrentNumber(dto.getStartNumber()); // Initialize current number to start number
        return entity;
    }
    
    public IDFormattingDTO toDTO(IDFormatting entity) {
        if (entity == null) {
            return null;
        }
        
        IDFormattingDTO dto = new IDFormattingDTO();
        dto.setIdFormattingId(entity.getIdFormattingId());
        dto.setCategoryId(entity.getCategoryId());
        dto.setPrefix(entity.getPrefix());
        dto.setObjectId(entity.getObjectId());
        dto.setStartNumber(entity.getStartNumber());
        return dto;
    }
    
    public void updateEntityFromDTO(IDFormattingDTO dto, IDFormatting entity) {
        if (dto == null || entity == null) {
            return;
        }
        
        entity.setIdFormattingId(dto.getIdFormattingId());
        entity.setCategoryId(dto.getCategoryId());
        entity.setPrefix(dto.getPrefix());
        entity.setObjectId(dto.getObjectId());
        entity.setStartNumber(dto.getStartNumber());
        // Don't update currentNumber when updating format - it should continue from where it was
    }
} 