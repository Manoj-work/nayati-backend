package com.medhir.rest.sales.dto;

import lombok.Data;

@Data
public class StatusUpdateRequest {
    private String stageId;

    public String getStageId() {
        return stageId;
    }
    private String status;
}
