package com.medhir.rest.assetManagement.assetSetting.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "idFormatting")
public class IDFormatting {
    
    @Id
    private String id; // MongoDB ObjectId
    
    @Field("idFormattingId")
    private String idFormattingId; // IDF-{snowflakeId}
    
    @Field("categoryId")
    private String categoryId;
    
    @Field("prefix")
    private String prefix;
    
    @Field("objectId")
    private String objectId;
    
    @Field("startNumber")
    private Integer startNumber;
    
    @Field("currentNumber")
    private Integer currentNumber;
    
    public IDFormatting() {}
    
    public IDFormatting(String categoryId, String prefix, String objectId, Integer startNumber) {
        this.categoryId = categoryId;
        this.prefix = prefix;
        this.objectId = objectId;
        this.startNumber = startNumber;
        this.currentNumber = startNumber;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
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
    
    public Integer getCurrentNumber() { return currentNumber; }
    public void setCurrentNumber(Integer currentNumber) { this.currentNumber = currentNumber; }
} 