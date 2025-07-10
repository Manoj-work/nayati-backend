package com.medhir.rest.assetManagement.assetSetting.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "statusLabels")
public class StatusLabel {
    
    @Id
    private String id;
    
    @Field("statusLabelId")
    private String statusLabelId;
    
    @Field("name")
    private String name;
    
    @Field("color")
    private String color;
    
    public StatusLabel() {}
    
    public StatusLabel(String name, String color) {
        this.name = name;
        this.color = color;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getStatusLabelId() { return statusLabelId; }
    public void setStatusLabelId(String statusLabelId) { this.statusLabelId = statusLabelId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
} 