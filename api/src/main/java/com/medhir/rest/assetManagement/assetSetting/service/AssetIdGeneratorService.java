package com.medhir.rest.assetManagement.assetSetting.service;

import com.medhir.rest.assetManagement.assetSetting.model.IDFormatting;
import com.medhir.rest.assetManagement.assetSetting.repository.IDFormattingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AssetIdGeneratorService {
    
    @Autowired
    private IDFormattingRepository idFormattingRepository;
    
    /**
     * Generates the next asset ID for a given category
     * @param categoryId The category ID to generate asset ID for
     * @return The generated asset ID (e.g., "IT-01-0001")
     */
    public synchronized String generateAssetId(String categoryId) {
        Optional<IDFormatting> formatOptional = idFormattingRepository.findByCategoryId(categoryId);
        
        if (formatOptional.isEmpty()) {
            // If no formatting found, create a default one
            IDFormatting defaultFormat = new IDFormatting();
            defaultFormat.setCategoryId(categoryId);
            defaultFormat.setPrefix("ASSET");
            defaultFormat.setObjectId("01");
            defaultFormat.setStartNumber(1);
            defaultFormat.setCurrentNumber(1);
            idFormattingRepository.save(defaultFormat);
            return buildAssetId(defaultFormat);
        }
        
        IDFormatting format = formatOptional.get();
        
        // Generate the asset ID with current number
        String assetId = buildAssetId(format);
        
        // Increment the current number for next use
        format.setCurrentNumber(format.getCurrentNumber() + 1);
        idFormattingRepository.save(format);
        
        return assetId;
    }
    
    /**
     * Builds the asset ID string based on the formatting rules
     * @param format The ID formatting configuration
     * @return The formatted asset ID
     */
    private String buildAssetId(IDFormatting format) {
        String prefix = format.getPrefix() != null ? format.getPrefix() : "ASSET";
        String objectId = format.getObjectId() != null ? format.getObjectId() : "01";
        Integer currentNumber = format.getCurrentNumber() != null ? format.getCurrentNumber() : 1;
        
        // Format the number with leading zeros (e.g., 0001)
        String formattedNumber = String.format("%04d", currentNumber);
        
        // Build the asset ID: PREFIX-OBJECTID-NUMBER
        return prefix + "-" + objectId + "-" + formattedNumber;
    }
    
    /**
     * Preview what the next asset ID would be for a category without generating it
     * @param categoryId The category ID to preview
     * @return The next asset ID that would be generated
     */
    public String previewNextAssetId(String categoryId) {
        Optional<IDFormatting> formatOptional = idFormattingRepository.findByCategoryId(categoryId);
        
        if (formatOptional.isEmpty()) {
            // Return what would be the default
            return "ASSET-01-0001";
        }
        
        return buildAssetId(formatOptional.get());
    }
} 