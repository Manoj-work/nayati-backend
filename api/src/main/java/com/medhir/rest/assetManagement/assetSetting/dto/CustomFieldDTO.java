package com.medhir.rest.assetManagement.assetSetting.dto;

public class CustomFieldDTO {
    private String id;
    private String categoryId;
    private String label;
    private String type;
    private boolean required;
    private boolean enabled;
    
    public CustomFieldDTO() {}
    
    public CustomFieldDTO(String id, String categoryId, String label, String type, boolean required, boolean enabled) {
        this.id = id;
        this.categoryId = categoryId;
        this.label = label;
        this.type = type;
        this.required = required;
        this.enabled = enabled;
    }
    
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