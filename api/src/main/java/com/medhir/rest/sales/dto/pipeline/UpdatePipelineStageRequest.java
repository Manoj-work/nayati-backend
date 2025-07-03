package com.medhir.rest.sales.dto.pipeline;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePipelineStageRequest {
    private String name;
    private String description;
    private String color;
    private Boolean isActive;
} 