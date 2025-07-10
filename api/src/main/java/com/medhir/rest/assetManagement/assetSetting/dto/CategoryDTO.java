package com.medhir.rest.assetManagement.assetSetting.dto;

public class CategoryDTO {
    private String categoryId;
    private String name;
    private double depreciationRate;
    
    public CategoryDTO() {}
    
    public CategoryDTO(String categoryId, String name, double depreciationRate) {
        this.categoryId = categoryId;
        this.name = name;
        this.depreciationRate = depreciationRate;
    }
    
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getDepreciationRate() { return depreciationRate; }
    public void setDepreciationRate(double depreciationRate) { this.depreciationRate = depreciationRate; }
} 