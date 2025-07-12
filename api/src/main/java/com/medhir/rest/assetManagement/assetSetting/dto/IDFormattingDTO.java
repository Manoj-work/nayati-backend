package com.medhir.rest.assetManagement.assetSetting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IDFormattingDTO {
    private String idFormattingId;
    private String categoryId;
    private String prefix;
    private String objectId;
    private Integer startNumber;
} 