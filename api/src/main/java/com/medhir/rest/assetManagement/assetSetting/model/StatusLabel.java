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
    
    public StatusLabel(String name, String color) {
        this.name = name;
        this.color = color;
    }
} 