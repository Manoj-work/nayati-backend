package com.medhir.rest.sales.dto.pipeline;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReorderPipelineStageRequest {
    private String stageId;
    @Min(value = 0, message = "New order index must be non-negative")
    private int newOrderIndex;
} 