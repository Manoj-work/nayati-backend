package com.medhir.rest.assetManagement.assetSetting.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    
    public CustomField() {}
    
    public CustomField(String categoryId, String label, String type, boolean required, boolean enabled) {
        this.categoryId = categoryId;
        this.label = label;
        this.type = type;
        this.required = required;
        this.enabled = enabled;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
} 