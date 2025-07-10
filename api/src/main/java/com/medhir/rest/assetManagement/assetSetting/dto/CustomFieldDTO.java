package com.medhir.rest.assetManagement.assetSetting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldDTO {
    private String id;
    private String categoryId;
    private String label;
    private String type;
    private boolean required;
    private boolean enabled;
} 