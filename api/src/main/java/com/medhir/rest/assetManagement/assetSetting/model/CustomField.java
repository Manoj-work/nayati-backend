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
@Document(collection = "customFields")
public class CustomField {
    
    @Id
    private String id;
    
    @Field("categoryId")
    private String categoryId;
    
    @Field("label")
    private String label;
    
    @Field("type")
    private String type;
    
    @Field("required")
    private boolean required;
    
    @Field("enabled")
    private boolean enabled;
    
    public CustomField(String categoryId, String label, String type, boolean required, boolean enabled) {
        this.categoryId = categoryId;
        this.label = label;
        this.type = type;
        this.required = required;
        this.enabled = enabled;
    }
} 