package com.medhir.rest.sales.dto.pipeline;

import com.medhir.rest.sales.model.FormType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PipelineStageResponse {
    private String stageId;
    private String name;
    private String description;
    private int orderIndex;
    private String color;
    private boolean isActive;
    private String createdBy;
    private String createdAt;
    private String updatedAt;
    private boolean isForm;
    private FormType formType;
    private int leadCount; // Number of leads in this stage
} 