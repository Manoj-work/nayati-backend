package com.medhir.rest.sales.dto.pipeline;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PipelineStageDTO {
    private String stageId;
    
    @NotBlank(message = "Stage name is required")
    private String name;
    
    private String description;
    
    @Min(value = 0, message = "Order index must be non-negative")
    private int orderIndex;
    
    private String color;
    private boolean isActive;
    private String createdBy;
    private String createdAt;
    private String updatedAt;
} 