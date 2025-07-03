package com.medhir.rest.sales.dto.lead;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLeadStageRequestDTO {
    @NotBlank(message = "Stage ID is required")
    private String stageId;
} 