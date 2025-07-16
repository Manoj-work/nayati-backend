package com.medhir.rest.assetManagement.assetSetting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    
    public Category(String name, double depreciationRate) {
        this.name = name;
        this.depreciationRate = depreciationRate;
    }
} 