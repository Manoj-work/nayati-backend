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
    
    public IDFormatting(String categoryId, String prefix, String objectId, Integer startNumber) {
        this.categoryId = categoryId;
        this.prefix = prefix;
        this.objectId = objectId;
        this.startNumber = startNumber;
        this.currentNumber = startNumber;
    }
} 