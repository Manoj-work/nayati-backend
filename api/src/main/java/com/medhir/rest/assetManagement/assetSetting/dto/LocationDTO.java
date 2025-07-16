package com.medhir.rest.assetManagement.assetSetting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private String locationId;
    private String name;
    private String address;
} 