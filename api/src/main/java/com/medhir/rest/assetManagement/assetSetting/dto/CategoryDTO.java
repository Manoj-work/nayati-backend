package com.medhir.rest.assetManagement.assetSetting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private String categoryId;
    private String name;
    private double depreciationRate;
} 