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
@Document(collection = "locations")
public class Location {
    
    @Id
    private String id;
    
    @Field("locationId")
    private String locationId;
    
    @Field("name")
    private String name;
    
    @Field("address")
    private String address;
    
    public Location(String name, String address) {
        this.name = name;
        this.address = address;
    }
} 