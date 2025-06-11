package com.medhir.rest.model.updates;

import lombok.Data;

@Data
public class UpdateRequest {
    private String employeeId;
    private String message;
    private String flag;
}
