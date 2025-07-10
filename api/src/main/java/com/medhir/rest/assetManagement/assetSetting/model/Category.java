package com.medhir.rest.assetManagement.assetSetting.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "categories")
public class Category {
    
    @Id
    private String id;
    
    @Field("categoryId")
    private String categoryId;
    
    @Field("name")
    private String name;
    
    @Field("depreciationRate")
    private double depreciationRate;
    
    public Category() {}
    
    public Category(String name, double depreciationRate) {
        this.name = name;
        this.depreciationRate = depreciationRate;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getDepreciationRate() { return depreciationRate; }
    public void setDepreciationRate(double depreciationRate) { this.depreciationRate = depreciationRate; }
} 