package com.medhir.rest.assetManagement.assetSetting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusLabelDTO {
    private String statusLabelId;
    private String name;
    private String color;
} 