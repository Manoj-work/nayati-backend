package com.medhir.rest.sales.dto.pipeline;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePipelineStageRequest {
    @NotBlank(message = "Stage name is required")
    private String name;
    
    private String description;
    private String color;
} 