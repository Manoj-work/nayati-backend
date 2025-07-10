package com.medhir.rest.assetManagement.assetSetting.dto;

public class IDFormattingDTO {
    private String idFormattingId;
    private String categoryId;
    private String prefix;
    private String objectId;
    private Integer startNumber;
    
    public IDFormattingDTO() {}
    
    public IDFormattingDTO(String idFormattingId, String categoryId, String prefix, String objectId, Integer startNumber) {
        this.idFormattingId = idFormattingId;
        this.categoryId = categoryId;
        this.prefix = prefix;
        this.objectId = objectId;
        this.startNumber = startNumber;
    }
    
    public String getIdFormattingId() { return idFormattingId; }
    public void setIdFormattingId(String idFormattingId) { this.idFormattingId = idFormattingId; }
    
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    
    public String getObjectId() { return objectId; }
    public void setObjectId(String objectId) { this.objectId = objectId; }
    
    public Integer getStartNumber() { return startNumber; }
    public void setStartNumber(Integer startNumber) { this.startNumber = startNumber; }
} 