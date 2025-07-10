package com.medhir.rest.assetManagement.assetSetting.dto;

public class StatusLabelDTO {
    private String statusLabelId;
    private String name;
    private String color;
    
    public StatusLabelDTO() {}
    
    public StatusLabelDTO(String statusLabelId, String name, String color) {
        this.statusLabelId = statusLabelId;
        this.name = name;
        this.color = color;
    }
    
    public String getStatusLabelId() { return statusLabelId; }
    public void setStatusLabelId(String statusLabelId) { this.statusLabelId = statusLabelId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
} 